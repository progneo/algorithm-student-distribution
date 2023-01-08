package data.model

data class Participation(
    val id: Int,
    var priority: Int,
    var projectId: Int,
    val studentId: Int,
    val studentName: String,
    val group: String,
    var stateId: Int
)