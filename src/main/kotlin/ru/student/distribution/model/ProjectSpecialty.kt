package ru.student.distribution.model

data class ProjectSpecialty(
    val id: Int,
    val course: Int,
    val specialty: Specialty,
    val priority: Int
)
