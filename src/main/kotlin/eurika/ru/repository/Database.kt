package eurika.ru.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object Graphs : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val uuid = varchar("uuid", 36)
}

object Nodes : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val graphId = reference("graphId", Graphs.id)
    val name = varchar("name", 100)
}

object NodeProperties : Table() {
    val id = integer("id").autoIncrement()
    val nodeId = reference("nodeId", Nodes.id)
    val name = varchar("name", 100)
    val value = text("value")
}

object Edges : Table() {
    val id = integer("id").autoIncrement()
    val graphId = reference("graphId", Graphs.id)
    val name = varchar("name", 100)
    val sources = text("sources")
    val target = text("target")
}

fun initDatabase() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/graph-base",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "12345"
    )
    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            Graphs,
            Nodes,
            NodeProperties,
            Edges
        )
    }
}