package ru.student.distribution.domain.distribution

import ru.student.distribution.model.*

internal class Distribution(
    private val students: MutableList<Student>,
    private val notApplied: MutableList<Student>,
    private val projects: MutableList<Project>,
    private val participations: MutableList<Participation>,
    private val institute: Institute,
    private val distributionRule: DistributionRule
) {

    companion object {
        var participationIndex: Int = 0
    }

    private val priorities = mutableMapOf<ProjectSpecialty, MutableList<Priority>>()

    private val notAppliedIds = notApplied.map { it.id }

    fun execute(): InstituteResults {
        initPriorities()
        distributeSilentStudents(isUniformly = true)
        distributeSilentStudentsUniformlyToFull()
        logResults()

        return InstituteResults(
            institute = institute,
            notAppliedStudents = notApplied,
            participation = participations,
            projects = projects
        )
    }

    private fun initPriorities() {
        projects.forEach { project ->
            project.projectSpecialties.forEach { group ->
                val koef = evaluateKoef(students, notApplied, participations, group, project)
                if (koef != 0f) {
                    val temp = priorities.getOrDefault(group, mutableListOf())
                    temp.add(Priority(project.id, koef))
                    priorities[group] = temp
                }
            }
        }
    }

    private fun distributeSilentStudents(isUniformly: Boolean = true) {
        var sortedPriorities = mutableMapOf<ProjectSpecialty, MutableList<Priority>>()
        priorities.forEach {
            sortedPriorities[it.key] = it.value.toMutableList()
        }
        sortedPriorities = sortedPriorities
            .toList()
            .sortedBy { (key, value) -> value.size }
            .toMap()
            .toMutableMap()


        val upperPlacesBoundary =
            if (isUniformly) distributionRule.minPlaces
            else distributionRule.maxPlaces

        sortedPriorities.forEach { entry ->
            val studentsForProjects = notApplied.filter { it.specialty == entry.key.specialty && it.course == entry.key.course }.toMutableList()

            while (studentsForProjects.isNotEmpty() && sortedPriorities[entry.key]!!.isNotEmpty()) {
                val maxPriority = entry.value.maxBy { it.koef }
                val project = projects.find { maxPriority.projectId == it.id }!!

                if (project.busyPlaces >= upperPlacesBoundary) {
                    sortedPriorities[entry.key]!!.removeIf { it.projectId == project.id }
                    continue
                }

                val student = studentsForProjects[0]

                participations.add(
                    Participation(
                        id = participationIndex++,
                        priority = if (notAppliedIds.contains(student.id)) 4 else 5,
                        projectId = maxPriority.projectId,
                        studentId = student.id,
                        stateId = 1
                    )
                )
                projects.find { it.id == maxPriority.projectId }!!.apply {
                    freePlaces--
                    busyPlaces++
                }
                updateKoef(sortedPriorities, entry.key, maxPriority.projectId, students, participations, projects)
                updateKoef(priorities, entry.key, maxPriority.projectId, students, participations, projects)
                notApplied.removeIf { stud -> stud.id == studentsForProjects[0].id }
                studentsForProjects.removeIf { stud -> stud.id == studentsForProjects[0].id }
            }
        }
    }

    private fun distributeSilentStudentsUniformlyToFull() {
        var sortedProjects = projects.filter { it.freePlaces != 0 }
        var lowerValue = sortedProjects.minBy { it.busyPlaces }.busyPlaces
        var upperValue = sortedProjects.maxBy { it.busyPlaces }.busyPlaces

        while (lowerValue < distributionRule.maxPlaces && notApplied.isNotEmpty()) {
            sortedProjects = sortedProjects
                .sortedWith(compareBy({ it.busyPlaces }, { it.groups.size }))

            val lastLowerValue = lowerValue

            sortedProjects.forEach sortedProjects@ { project ->
                if (project.busyPlaces == upperValue && lastLowerValue != lowerValue) {
                    return@sortedProjects
                }
                if (project.busyPlaces == lowerValue) {
                    val groupPriorities = mutableMapOf<ProjectSpecialty, Float>()
                    project.projectSpecialties.forEach { group ->
                        priorities[group]!!.find { it.projectId == project.id }?.let {
                            groupPriorities[group] = it.koef
                        }
                    }

                    try {
                        val groupToEnroll = groupPriorities.maxBy { (key, value) -> value }

                        val student = notApplied.first { it.specialty == groupToEnroll.key.specialty && it.course == groupToEnroll.key.course }
                        println("ENROLL ${student.fullGroupName} TO ${project.id}")

                        participations.add(
                            Participation(
                                id = participationIndex++,
                                priority = if (notAppliedIds.contains(student.id)) 4 else 5,
                                projectId = project.id,
                                stateId = 1,
                                studentId = student.id
                            )
                        )

                        notApplied.removeIf { it.id == student.id }
                        updateKoef(priorities, groupToEnroll.key, project.id, students, participations, sortedProjects)
                        sortedProjects.find { it.id == project.id }!!.apply {
                            busyPlaces++
                            freePlaces--
                        }
                        lowerValue = sortedProjects.minBy { it.busyPlaces }.busyPlaces
                    } catch (e: NoSuchElementException) {
                        println(e)
                    }
                }
            }

            if (lastLowerValue == lowerValue) {
                break
            }
        }
    }

    private fun evaluateKoef(
        students: List<Student>,
        notAppliedStudents: List<Student>,
        participation: List<Participation>,
        projectSpecialty: ProjectSpecialty,
        project: Project,
    ): Float {
        val groupCourseStudents = students.count { it.specialty == projectSpecialty.specialty && it.course == projectSpecialty.course }
        val groupCourseFreeStudents =
            notAppliedStudents.count { it.specialty == projectSpecialty.specialty && it.course == projectSpecialty.course }
        val koef = (project.freePlaces * 1.0f / project.places) * (groupCourseFreeStudents * 1.0f / groupCourseStudents)

        return koef
    }

    private fun updateKoef(
        map: MutableMap<ProjectSpecialty, MutableList<Priority>>,
        projectSpecialty: ProjectSpecialty,
        projectId: Int,
        students: List<Student>,
        participation: List<Participation>,
        projects: List<Project>,
    ) {
        map.toMap()
        val project = projects.find { it.id == projectId }!!

        //selected project groups
        project.projectSpecialties.forEach { psp ->
            val koef = evaluateKoef(students, notApplied, participation, psp, project)

            if (koef == 0f) {
                map[psp]!!.removeIf { item -> item.projectId == projectId }
            } else {
                map[psp]!!.find { item -> item.projectId == project.id }!!.koef = koef
            }
        }


        //other projects
        val iterator = map[projectSpecialty]!!.iterator()

        while (iterator.hasNext()) {
            val priority = iterator.next()
            if (priority.projectId != projectId) {
                val groupProject = projects.find { it.id == priority.projectId }!!
                val koef = evaluateKoef(students, notApplied, participation, projectSpecialty, groupProject)

                if (koef == 0f) {
                    iterator.remove()
                } else {
                    map[projectSpecialty]!!.find { it.projectId == groupProject.id }!!.koef = koef
                }
            }
        }
    }

    private fun logResults() {
        println(institute)
        projects.forEach {
            println("freePlaces=${it.freePlaces} participants=${participations.count { part -> part.projectId == it.id && part.stateId == 1 }} groups=${it.groups} projectId=${it.id} projectName=${it.title}")
        }
        println("--------------------")
    }

    private fun logProjectStudents() {

    }
}