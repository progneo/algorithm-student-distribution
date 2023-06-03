package ru.student.distribution.model

data class Project(
    var id: Int,
    var title: String,
    var places: Int,
    var freePlaces: Int,
    var busyPlaces: Int,
    var groups: List<Specialty>,
    var difficulty: Int,
    var customer: String,
    val department: Department,
    var supervisors: List<Supervisor>,
    val projectSpecialties: List<ProjectSpecialty>,
    val anotherProjectSpecialties: List<ProjectSpecialty>,
    val goal: String,
    val description: String,
    val dateStart: String,
    val dateEnd: String,
    val productResult: String,
    val studyResult: String
)