package ru.student.distribution.model

data class Supervisor(
    var id: Int,
    val name: String,
    var department: Department,
    var position: String
)
