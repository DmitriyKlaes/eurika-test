package eurika.ru

import eurika.ru.controller.graphRouting
import eurika.ru.plugins.configureSerialization
import eurika.ru.repository.initDatabase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    initDatabase()
    graphRouting()
}
