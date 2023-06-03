package ru.student.distribution.domain.distribution

import com.grapecity.documents.excel.drawing.b.it
import ru.student.distribution.model.*

class DistributionLauncher(
    private val students: MutableList<Student>,
    private val projects: MutableList<Project>,
    private val participations: MutableList<Participation>,
    private val institutes: List<Institute>,
    private val distributionRule: DistributionRule,
    private val specialInstitute: Institute,
) {

    fun launch(): DistributionResults {
        val participationDistributionConfiguration = ParticipationDistribution(
            projects = projects,
            participation = participations,
            students = students
        ).distribute()

        val totalParticipation = mutableListOf<Participation>()
        val instituteResults = mutableListOf<InstituteResults>()

        totalParticipation.addAll(participationDistributionConfiguration.participation )

        Distribution.participationIndex = participations.last().id + 1

//        println("---NOT APPLIED--- ${participationDistributionConfiguration.notAppliedStudents.size}")
//        participationDistributionConfiguration.notAppliedStudents.forEach(::println)
//        println("---NOT APPLIED--- ${participationDistributionConfiguration.notAppliedStudents.size}")

        institutes.forEach { institute ->
            if (institute.id == specialInstitute.id) return@forEach

            val newProjects =
                participationDistributionConfiguration.projects
                    .filter {
                        it.department.id != 0 &&
                                it.projectSpecialties
                                    .map { ps -> ps.specialty }
                                    .map { s -> s.institute }
                                    .map { i -> i.id }
                                    .contains(institute.id)
                    }
                    .toMutableList()

            newProjects.forEach {
                println("${it.id}, ${it.title.take(20)}")
            }

            println("newProjectsSize = ${newProjects.size}")

            val newInstituteResults = Distribution(
                students = students
                    .filter { it.specialty.institute.id == institute.id }
                    .toMutableList(),
                notAppliedStudents = participationDistributionConfiguration.notAppliedStudents
                    .filter { it.specialty.institute.id == institute.id }
                    .toMutableList(),
                freeStudents = participationDistributionConfiguration.freeStudents
                    .filter { it.specialty.institute.id == institute.id }
                    .toMutableList(),
                projects = newProjects,
                institute = institute,
                distributionRule = distributionRule
            ).execute()

            instituteResults.add(newInstituteResults)
            totalParticipation.addAll(newInstituteResults.participation)
        }

        val pids = totalParticipation.map { it.studentId }
        students.filter { !pids.contains(it.id) }.map { it.course }.forEach(::println)

        return DistributionResults(
            participation = totalParticipation,
            instituteResults = instituteResults,
            excessProjects = listOf()
        )
    }
}