package ru.student.distribution.data.model

data class Specialty(
    val id: Int,
    val name: String,
    val institute: Institute,
    val department: Department
)
