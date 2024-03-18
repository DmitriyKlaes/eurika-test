package eurika.ru.model

data class RequestGraph(
    val uuid: String,
    val graph: Graph,
)

data class Graph(
    val nodes: List<Node>,
    val edges: List<Edge>,
)

data class Node(
    val name: String,
    val property: List<Property>,
)

data class Property(
    val name: String,
    val value: String,
)

data class Edge(
    val name: String,
    val sources: String,
    val target: String,
)

data class RequestUUID(
    val uuid: String
)

