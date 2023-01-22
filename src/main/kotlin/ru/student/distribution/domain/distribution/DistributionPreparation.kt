package ru.student.distribution.domain.distribution

import ru.student.distribution.data.model.Participation
import ru.student.distribution.data.model.Student

class DistributionPreparation(
    private val students: List<Student>,
    private val participations: List<Participation>
) {

    val freeStudents = students.toMutableList()

    fun prepare() {
        val parts = participations.map { it.studentId }.toSet()
        parts.forEach {
            freeStudents.removeIf { stud -> it == stud.id }
        }
    }
}