package ru.student.distribution.model

data class Participation(
    val id: Int,
    var priority: Int,
    var projectId: Int,
    val studentId: Int,
    var stateId: Int,
    var studentNumz: Int,
    var studentName: String,
    var updatedAt: String
)