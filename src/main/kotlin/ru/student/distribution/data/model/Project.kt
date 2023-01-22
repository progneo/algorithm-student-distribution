package ru.student.distribution.data.model

data class Project(
    var id: Int,
    var title: String,
    var places: Int,
    var freePlaces: Int,
    var groups: List<String>,
    var difficulty: Int,
    var customer: String,
    var supervisors: List<String>,
)