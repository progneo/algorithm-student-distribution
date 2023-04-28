package ru.student.distribution.domain.distribution

import ru.student.distribution.data.model.Participation
import ru.student.distribution.data.model.Project
import ru.student.distribution.data.model.Student

class ParticipationDistribution(
    private val projects: List<Project>,
    private val participation: List<Participation>,
    private val students: List<Student>,
) {

    data class Configuration(
        val projects: List<Project>,
        val participation: List<Participation>,
        val notAppliedStudents: List<Student>
    )

    fun distribute(): Configuration {
        for (priority in (1..3)) {
            for (project in projects) {
                if (project.freePlaces != 0) {
                    val participationsForCurrentProject =
                        participation.filter {
                            it.projectId == project.id &&
                                    it.priority == priority &&
                                    it.stateId == 0
                        }
                            .toMutableList()

                    if (participationsForCurrentProject.isNotEmpty()) {
                        for (i in participationsForCurrentProject) {
                            if (project.freePlaces == 0) {
                                break
                            }
                            val currentParticipationIndex = participation.indexOf(i)
                            participation[currentParticipationIndex].stateId = 1

                            val participationsToDelete =
                                participation.filter { it.studentId == participation[currentParticipationIndex].studentId && it.priority != priority }
                            for (j in participationsToDelete) {
                                participation[participation.indexOf(j)].stateId = 2
                            }
                            project.freePlaces--
                            project.busyPlaces++
                        }
                    }
                }
            }
        }

        val notAppliedStudents = findNotAppliedStudents()

        return Configuration(
            projects,
            participation,
            notAppliedStudents
        )
    }

    private fun findNotAppliedStudents(): List<Student> {
        val notApplied = mutableListOf<Student>()

        val notAppliedParticipations = participation.filter { it.stateId == 0 }

        val notAppliedStudents =
            participation.filter { it.stateId == 0 && students.map { stud -> stud.id }.contains(it.studentId) }
                .map { it.studentId }.toSet()

        notApplied.addAll(notAppliedStudents.map { students.find { stud -> stud.id == it }!! })

        notApplied.addAll(DistributionPreparation(
            students,
            participation
        ).prepare())

        notAppliedParticipations.forEach {
            it.stateId = 2
        }

        return notApplied
    }
}