package ru.student.distribution.domain.distribution

import ru.student.distribution.model.Participation
import ru.student.distribution.model.Project
import ru.student.distribution.model.Student

internal class ParticipationDistribution(
    private val projects: List<Project>,
    private val participationList: List<Participation>,
    private val students: List<Student>,
) {

    data class Configuration(
        val projects: List<Project>,
        val participation: List<Participation>,
        val freeStudents: List<Student>,
        val notAppliedStudents: List<Student>,
    )

    private val newParticipation = mutableListOf<Participation>()

    fun distribute(): Configuration {
        for (priority in (1..5)) {
            for (project in projects) {
                val participationsForCurrentProject = participationList
                    .filter { it.projectId == project.id && it.priority == priority && it.stateId == 0 }
                    .toMutableList()

                for (participation in participationsForCurrentProject) {
                    val currentParticipationIndex = participationList.indexOf(participation)

                    if (project.busyPlaces >= project.places) {
                        participationList[currentParticipationIndex].stateId = 2
                    } else {
                        participationList[currentParticipationIndex].stateId = 1

                        val participationsToDelete = participationList.filter {
                            it.studentId == participationList[currentParticipationIndex].studentId && it.priority != priority
                        }

                        for (participationToDelete in participationsToDelete) {
                            participationList[participationList.indexOf(participationToDelete)].stateId = 2
                        }

                        project.freePlaces--
                        project.busyPlaces++
                    }
                }
            }
        }

        newParticipation.addAll(participationList.filter { it.stateId == 1 })
        participationList.filter { it.stateId != 1 }.forEach(::println)

        val notAppliedStudents = findNotAppliedStudents()

        return Configuration(
            projects,
            newParticipation,
            DistributionPreparation(
                students,
                participationList,
            ).prepare(),
            notAppliedStudents,
        )
    }

    private fun findNotAppliedStudents(): List<Student> {
        val notApplied = mutableListOf<Student>()
        val notAppliedParticipations = participationList.filter { it.stateId == 0 }
        val notAppliedStudents = participationList
            .filter { it.stateId == 0 && students.map { stud -> stud.id }.contains(it.studentId) }
            .map { it.studentId }
            .toSet()

        notApplied.addAll(notAppliedStudents.map { students.find { stud -> stud.id == it }!! })

        notAppliedParticipations.forEach {
            it.stateId = 2
        }

        return notApplied
    }
}
