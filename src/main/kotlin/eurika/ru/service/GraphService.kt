package eurika.ru.service

import eurika.ru.exceptions.InvalidUuidException
import eurika.ru.model.Graph
import eurika.ru.repository.getGraphByUUID
import eurika.ru.repository.saveGraph
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.util.*

fun saveGraphToDatabase(uuid: String, graph: Graph): Boolean {
    return try {
        isValidUUID(uuid)
        saveGraph(uuid, graph)
        true
    } catch (e: InvalidUuidException) {
        false
    } catch (e: ExposedSQLException) {
        false
    }
}

fun getGraphFromDatabase(uuid: String): Graph? {
    return try {
        isValidUUID(uuid)
        getGraphByUUID(uuid)
    } catch (e: InvalidUuidException) {
        null
    } catch (e: ExposedSQLException) {
        println("Ошибка при извлечении данных из базы")
        null
    }
}

private fun isValidUUID(uuid: String) {
    try {
        UUID.fromString(uuid)
    } catch (e: IllegalArgumentException) {
        throw InvalidUuidException()
    }
}