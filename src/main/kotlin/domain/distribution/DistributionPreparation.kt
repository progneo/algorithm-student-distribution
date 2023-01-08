package domain.distribution

import data.model.Participation
import data.model.Student

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