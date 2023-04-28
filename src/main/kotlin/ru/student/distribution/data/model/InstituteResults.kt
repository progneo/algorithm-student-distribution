package ru.student.distribution.data.model

data class InstituteResults(
    val institute: Institute,
    val notAppliedStudents: List<Student>,
    val participation: List<Participation>
)
