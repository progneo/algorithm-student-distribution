package ru.student.distribution.domain.distribution

import ru.student.distribution.data.model.*

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

        val institutesResults = mutableListOf<InstituteResults>()
        val totalParticipation = mutableListOf<Participation>()

        Distribution.participationIndex = participations.last().id + 1

        institutes.forEach { institute ->
            if (institute.id == specialInstitute.id) return@forEach

            val newProjects =
                participationDistributionConfiguration.projects
                    .filter { it.department.institute.id == institute.id }
                    .toMutableList()

            val newInstituteResults = Distribution(
                students = students
                    .filter { it.specialty.institute.id == institute.id }
                    .toMutableList(),
                notApplied = participationDistributionConfiguration.notAppliedStudents
                    .filter { it.specialty.institute.id == institute.id }
                    .toMutableList(),
                projects = newProjects,
                participations = participationDistributionConfiguration.participation
                    .filter {
                        newProjects.map { project -> project.id }.contains(it.projectId)
                    }.toMutableList(),
                institute = institute,
                distributionRule = distributionRule
            ).execute()

            institutesResults.add(newInstituteResults)
            totalParticipation.addAll(newInstituteResults.participation)
        }

        return DistributionResults(
            allParticipation = totalParticipation,
            institutesResults = institutesResults
        )
    }
}