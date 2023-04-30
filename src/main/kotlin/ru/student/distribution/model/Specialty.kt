package ru.student.distribution.model

data class Specialty(
    val id: Int,
    val name: String,
    val institute: Institute,
    val department: Department
)
