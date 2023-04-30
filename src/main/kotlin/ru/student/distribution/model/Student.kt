package ru.student.distribution.model

data class Student(
    var id: Int,
    var name: String,
    var groupFamily: String,
    var fullGroupName: String,
    val specialty: Specialty,
    val course: Int
)