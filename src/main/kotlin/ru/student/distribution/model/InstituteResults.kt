package ru.student.distribution.model

data class InstituteResults(
    val institute: Institute,
    val notAppliedStudents: List<Student>,
    val participation: List<Participation>,
    val projects: List<Project>
)
