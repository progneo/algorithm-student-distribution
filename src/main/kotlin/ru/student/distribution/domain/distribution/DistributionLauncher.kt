package ru.student.distribution.domain.distribution

import ru.student.distribution.data.model.*

class DistributionLauncher(
    private val students: MutableList<Student>,
    private val projects: MutableList<Project>,
    private val participations: MutableList<Participation>,
    private val institutes: List<Institute>,
    private val specialties: List<Specialty>,
    private val projectSpecialties: List<ProjectSpecialty>,
    private val savingPath: String,
    private val distributionRule: DistributionRule,
) {

    fun launch() {
        institutes.forEach { institute ->
            val newProjects = projects.filter { it.department.institute.id == institute.id }.toMutableList()
            Distribution(
                students = students.filter { it.specialty.institute.id == institute.id }.toMutableList(),
                allStudents = students,
                projects = newProjects,
                participations = participations.filter {
                    newProjects.map { project -> project.id }.contains(it.projectId)
                }.toMutableList(),
                institute = institute,
                specialties = specialties,
                projectSpecialties = projectSpecialties,
                savingPath = savingPath,
                distributionRule = distributionRule
            ).execute()
        }
    }
}