package eurika.ru.controller

import eurika.ru.model.*
import eurika.ru.plugins.configureSerialization
import eurika.ru.repository.initDatabase
import eurika.ru.service.saveGraphToDatabase
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphRoutingTest {

    @Test
    fun testSaveDataSuccessful() = testApplication {
        application {
            graphRouting()
            configureSerialization()
            initDatabase()
        }

        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        val response = client.post("/saveData") {
            contentType(ContentType.Application.Json)
            setBody(getTestGraphRequest())
        }

        val responseBody: Boolean = response.body()
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(responseBody)
    }

    @Test
    fun testGetDataSuccessful() = testApplication {
        application {
            graphRouting()
            configureSerialization()
        }

        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        val graphRequest = getTestGraphRequest()
        val graphNodes = graphRequest.graph.nodes
        val graphEdges = graphRequest.graph.edges

        initDatabase()
        saveGraphToDatabase(graphRequest.uuid, graphRequest.graph)

        val response = client.post("/getData") {
            contentType(ContentType.Application.Json)
            setBody(RequestUUID((graphRequest.uuid)))
        }
        val responseBody: Graph = response.body()
        val responseGraphNodes = responseBody.nodes
        val responseGraphEdges = responseBody.edges

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(graphNodes.count(), responseGraphNodes.count())
        assertEquals(graphEdges.count(), responseGraphEdges.count())

        for (index in 0 until graphNodes.count()) {
            assertEquals(graphNodes[index].name, responseGraphNodes[index].name)
            assertEquals(graphNodes[index].property.count(), responseGraphNodes[index].property.count())
            for (indexProp in 0 until graphNodes[index].property.count()) {
                assertEquals(graphNodes[index].property[indexProp].name, responseGraphNodes[index].property[indexProp].name)
                assertEquals(graphNodes[index].property[indexProp].value, responseGraphNodes[index].property[indexProp].value)
            }
        }
        for (index in 0 until graphEdges.count()) {
            assertEquals(graphEdges[index].name, responseGraphEdges[index].name)
            assertEquals(graphEdges[index].sources, responseGraphEdges[index].sources)
            assertEquals(graphEdges[index].target, responseGraphEdges[index].target)
        }
    }
}



private fun getTestGraphRequest(): RequestGraph {
    val nodeProperties = listOf(Property("test-property-name-1", "test-property-value-1"),
                                              Property("test-property-name-2", "test-property-value-2"))
    val nodes = listOf(Node("test-node-name-1", nodeProperties),
                                  Node("test-node-name-2", nodeProperties))
    val edges = listOf(Edge("test-edge-name-1", "test-edge-source-1", "test-edge-target-1"),
                                  Edge("test-edge-name-2", "test-edge-source-2", "test-edge-target-2"))

    return RequestGraph(UUID.randomUUID().toString(),
                        Graph(nodes, edges)
    )
}