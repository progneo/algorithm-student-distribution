package ru.student.distribution.data.model

data class Supervisor(
    var id: Int,
    val name: String,
    var department: Department,
    var position: String
)
