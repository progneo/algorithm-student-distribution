package ru.student.distribution.domain.distribution

import ru.student.distribution.model.*
import java.text.SimpleDateFormat
import java.util.*

internal class Distribution(
    private val students: MutableList<Student>,
    private val notAppliedStudents: MutableList<Student>,
    private val freeStudents: MutableList<Student>,
    private val projects: MutableList<Project>,
    private val institute: Institute,
    private val distributionRule: DistributionRule,
) {
    companion object {
        var participationIndex: Int = 0
    }

    private val priorities = mutableMapOf<ProjectSpecialty, MutableList<Priority>>()

    private val newParticipations = mutableListOf<Participation>()

    private val notAppliedIds = notAppliedStudents.map { it.id }

    private val notApplied = (notAppliedStudents + freeStudents).toMutableList()

    fun execute(): InstituteResults {
        initPriorities()
        distributeSilentStudents(isUniformly = true)
        distributeSilentStudentsUniformlyToFull()
        logResults()

        return InstituteResults(
            participation = newParticipations,
            institute = institute,
            notAppliedStudents = notApplied,
            projects = projects,
        )
    }

    private fun initPriorities() {
        projects.forEach { project ->
            project.projectSpecialties.forEach { group ->
                val koef = evaluateKoef(students, notApplied, group, project)
                val temp = priorities.getOrDefault(group, mutableListOf())
                temp.add(Priority(project.id, koef))
                priorities[group] = temp
            }
        }

        priorities.forEach(::println)
    }

    private fun distributeSilentStudents(isUniformly: Boolean = true) {
        var sortedPriorities = mutableMapOf<ProjectSpecialty, MutableList<Priority>>()
        priorities.forEach {
            sortedPriorities[it.key] = it.value.toMutableList()
        }
        sortedPriorities =
            sortedPriorities
                .toList()
                .sortedBy { (key, value) -> value.size }
                .toMap()
                .toMutableMap()

        val upperPlacesBoundary =
            if (isUniformly) {
                distributionRule.minPlaces
            } else {
                distributionRule.maxPlaces
            }

        sortedPriorities.forEach { entry ->
            val studentsForProjects =
                notApplied.filter { it.specialty == entry.key.specialty && it.course == entry.key.course }
                    .toMutableList()

            while (studentsForProjects.isNotEmpty() && sortedPriorities[entry.key]!!.isNotEmpty()) {
                val maxPriority = entry.value.maxBy { it.koef }
                val project = projects.find { maxPriority.projectId == it.id }!!

                if (project.busyPlaces >= upperPlacesBoundary) {
                    sortedPriorities[entry.key]!!.removeIf { it.projectId == project.id }
                    continue
                }

                val student = studentsForProjects[0]

                newParticipations.add(
                    Participation(
                        id = participationIndex++,
                        priority = if (notAppliedIds.contains(student.id)) 4 else 5,
                        projectId = maxPriority.projectId,
                        studentId = student.id,
                        stateId = 1,
                        studentName = student.name,
                        studentNumz = student.numz,
                        updatedAt = getCurrentDateTime(),
                    ),
                )

                projects.find { it.id == maxPriority.projectId }!!.apply {
                    freePlaces--
                    busyPlaces++
                }

                updateKoef(sortedPriorities, entry.key, maxPriority.projectId, students, projects)
                updateKoef(priorities, entry.key, maxPriority.projectId, students, projects)

                notApplied.removeIf { stud -> stud.id == studentsForProjects[0].id }
                studentsForProjects.removeIf { stud -> stud.id == studentsForProjects[0].id }
            }
        }
    }

    private fun distributeSilentStudentsUniformlyToFull() {
        var sortedProjects = projects.filter { it.freePlaces > 0 }
        println("SORTED PROJECTS SIZE = ${sortedProjects.size}")
        if (sortedProjects.isEmpty()) return

        var lowerValue = sortedProjects.minBy { it.busyPlaces }.busyPlaces
        var upperValue = sortedProjects.maxBy { it.busyPlaces }.busyPlaces
        println("UPPER = $upperValue, LOWER = $lowerValue")

        process@ while (lowerValue < distributionRule.maxPlaces && notApplied.isNotEmpty()) {
            sortedProjects = sortedProjects.sortedWith(compareBy({ it.busyPlaces }, { it.groups.size }))

            val lastLowerValue = lowerValue

            for (project in sortedProjects) {
                val groupPriorities = mutableMapOf<ProjectSpecialty, Float>()
                project.projectSpecialties.forEach { group ->
                    priorities[group]?.find { it.projectId == project.id }?.let {
                        groupPriorities[group] = it.koef
                    }
                }

                val sortedGroupPriorities: MutableMap<ProjectSpecialty, Float> = LinkedHashMap()
                groupPriorities.entries.sortedBy { -it.value }.forEach {
                    sortedGroupPriorities[it.key] = it.value
                }

                for (gp in sortedGroupPriorities) {
                    try {
                        val student =
                            notApplied.first {
                                it.specialty.id == gp.key.specialty.id && it.course == gp.key.course
                            }

                        newParticipations.add(
                            Participation(
                                id = participationIndex++,
                                priority = if (notAppliedIds.contains(student.id)) 4 else 5,
                                projectId = project.id,
                                stateId = 1,
                                studentId = student.id,
                                studentName = student.name,
                                studentNumz = student.numz,
                                updatedAt = getCurrentDateTime(),
                            ),
                        )

                        notApplied.removeIf { it.id == student.id }
                        updateKoef(priorities, gp.key, project.id, students, sortedProjects)

                        sortedProjects.find { it.id == project.id }!!.apply {
                            busyPlaces++
                            freePlaces--
                        }

                        lowerValue = sortedProjects.minBy { it.busyPlaces }.busyPlaces
                        val currentMax = sortedProjects.maxBy { it.busyPlaces }.busyPlaces
                        if (currentMax > upperValue) upperValue = currentMax

                        continue@process
                    } catch (e: NoSuchElementException) {
                        e.printStackTrace()
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
        projectSpecialty: ProjectSpecialty,
        project: Project,
    ): Float {
        if (project.places == 0) return 0f
        val groupCourseStudents =
            students.count { it.specialty == projectSpecialty.specialty && it.course == projectSpecialty.course }
        if (groupCourseStudents == 0) return 0f
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
        projects: List<Project>,
    ) {
        map.toMap()
        val project = projects.find { it.id == projectId }!!

        // selected project groups
        project.projectSpecialties.forEach { psp ->
            val koef = evaluateKoef(students, notApplied, psp, project)

            if (koef == 0f) {
                map[psp]?.removeIf { item -> item.projectId == projectId }
            } else {
                map[psp]?.find { item -> item.projectId == project.id }?.koef = koef
            }
        }

        // other projects
        val iterator = map[projectSpecialty]!!.iterator()

        while (iterator.hasNext()) {
            val priority = iterator.next()
            if (priority.projectId != projectId) {
                val groupProject = projects.find { it.id == priority.projectId }!!
                val koef = evaluateKoef(students, notApplied, projectSpecialty, groupProject)

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
//        projects.forEach {
//            println("freePlaces=${it.freePlaces} participants=${newParticipations.count { part -> part.projectId == it.id && part.stateId == 1 }} groups=${it.groups} projectId=${it.id} projectName=${it.title}")
//        }
        println("--------------------")
    }

    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX")
        return sdf.format(Date())
    }
}
