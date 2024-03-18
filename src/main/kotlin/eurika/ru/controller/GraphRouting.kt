package eurika.ru.controller

import eurika.ru.model.RequestGraph
import eurika.ru.model.RequestUUID
import eurika.ru.service.getGraphFromDatabase
import eurika.ru.service.saveGraphToDatabase
import eurika.ru.service.sendGraphRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.TimeoutCancellationException

fun Application.graphRouting() {
    routing {
        post("/saveData") {
            try {
                val requestData = call.receive<RequestGraph>()
                val graphSaved = saveGraphToDatabase(requestData.uuid, requestData.graph)

                if (graphSaved) {
                    try {
                        if (sendGraphRequest(requestData, "http://test-work:8080/data")) {
                            println("Успешное сохранение в базу данных")
                            call.respond(HttpStatusCode.OK, true)
                        } else {
                            println("Отрицательный ответ от эндпоинта /data")
                            call.respond(HttpStatusCode.InternalServerError, false)
                        }
                    } catch (e: TimeoutCancellationException) {
                        println("Эндпоинт /data не отвечает")
                        call.respond(HttpStatusCode.GatewayTimeout, false)
                    }
                } else {
                    println("Ошибка при сохранении в базу данных")
                    call.respond(HttpStatusCode.InternalServerError, false)
                }
            } catch (e: BadRequestException) {
                println("Невозможно десериализовать данные из запроса")
                call.respond(HttpStatusCode.BadRequest, false)
            }
        }

        post ("/getData") {
            val request = call.receive<RequestUUID>()
            val graph = getGraphFromDatabase(request.uuid)
            if (graph != null) {
                call.respond(HttpStatusCode.OK, graph)
            }
            call.respond(HttpStatusCode.BadRequest)
        }
    }
}