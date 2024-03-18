package eurika.ru.service

import eurika.ru.model.RequestGraph
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.util.network.*
import kotlinx.coroutines.withTimeout
import kotlin.random.Random


suspend fun sendGraphRequest(graph: RequestGraph, dataUrl: String): Boolean {
    return try {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson()
            }
        }.use {
            withTimeout(5000) {
                val response: HttpResponse = it.post(dataUrl) {
                    contentType(ContentType.Application.Json)
                    setBody(graph)
                }
                response.body<Boolean>()
            }
        }
    } catch (e: UnresolvedAddressException) {
        return Random.nextBoolean()
    } catch (e: NoTransformationFoundException) {
        throw RuntimeException("Невозможно преобразовать ответ ендпоинта /data в тип boolean!")
    }
}