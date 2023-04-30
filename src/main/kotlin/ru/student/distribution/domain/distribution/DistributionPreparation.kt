package ru.student.distribution.domain.distribution

import ru.student.distribution.model.Participation
import ru.student.distribution.model.Student

internal class DistributionPreparation(
    private val students: List<Student>,
    private val participations: List<Participation>
) {

    private val freeStudents = students.toMutableList()

    fun prepare(): List<Student> {
        val parts = participations.map { it.studentId }.toSet()
        parts.forEach {
            freeStudents.removeIf { stud -> it == stud.id }
        }
        return freeStudents
    }
}