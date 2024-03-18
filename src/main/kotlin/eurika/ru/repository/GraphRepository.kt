package eurika.ru.repository

import eurika.ru.model.Edge
import eurika.ru.model.Graph
import eurika.ru.model.Node
import eurika.ru.model.Property
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun saveGraph(uuid: String, graph: Graph) {
    transaction {
        val graphId = Graphs.insert {
            it[Graphs.uuid] = uuid
        } get Graphs.id

        saveNodes(graph.nodes, graphId)
        saveEdges(graph.edges, graphId)
    }
}

private fun saveNodes(nodes: List<Node>, referenceId: Int) {
    nodes.forEach { node ->
        val nodeId = Nodes.insert {
            it[graphId] = referenceId
            it[name] = node.name
        } get Nodes.id

        saveProperties(node.property, nodeId)
    }
}

private fun saveProperties(properties: List<Property>, referenceId: Int) {
    properties.forEach { property ->
        NodeProperties.insert {
            it[nodeId] = referenceId
            it[name] = property.name
            it[value] = property.value
        }
    }
}

private fun saveEdges(edges: List<Edge>, referenceId: Int) {
    edges.forEach { edge ->
        Edges.insert {
            it[graphId] = referenceId
            it[name] = edge.name
            it[sources] = edge.sources
            it[target] = edge.target
        }
    }
}

fun getGraphByUUID(uuid: String): Graph? {
    val nodes = mutableListOf<Node>()
    val edges = mutableListOf<Edge>()

    val graph = transaction {
        val graphId = getGraphIdByUUID(uuid)
        if (graphId != null) {
            fillGraphNodesById(graphId, nodes)
            fillGraphEdgesById(graphId, edges)
            Graph(nodes, edges)
        } else {
            null
        }
    }

    return graph
}

fun getGraphIdByUUID(uuid: String): Int? {
    return Graphs.select {
        Graphs.uuid eq uuid
    }.singleOrNull()?.get(Graphs.id)
}

private fun fillGraphNodesById(referenceId: Int, listForFill: MutableList<Node>) {
    Nodes.select {
        Nodes.graphId eq referenceId
    }.forEach { nodeRow ->
        val nodeName = nodeRow[Nodes.name]
        val properties = getNodePropertiesById(nodeRow[Nodes.id])
        listForFill.add(Node(nodeName, properties))
    }
}

private fun getNodePropertiesById(referenceId: Int): List<Property> {
    val properties = mutableListOf<Property>()

    NodeProperties.select {
        NodeProperties.nodeId eq referenceId
    }.forEach { propertyRow ->
        properties.add(Property(propertyRow[NodeProperties.name], propertyRow[NodeProperties.value]))
    }

    return properties
}

private fun fillGraphEdgesById(referenceId: Int, listForFill: MutableList<Edge>) {
    Edges.select {
        Edges.graphId eq referenceId
    }.forEach { edgeRow ->
        listForFill.add(Edge(edgeRow[Edges.name], edgeRow[Edges.sources], edgeRow[Edges.target]))
    }
}