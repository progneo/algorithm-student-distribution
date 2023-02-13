package ru.student.distribution.domain.distribution

import ru.student.distribution.PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY
import ru.student.distribution.PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY
import ru.student.distribution.data.model.Participation
import ru.student.distribution.data.model.Project
import ru.student.distribution.data.model.Student
import ru.student.distribution.domain.data.ExportDataToExcel

class Distribution(
    private val students: MutableList<Student>,
    private val projects: MutableList<Project>,
    private val participations: MutableList<Participation>,
    private val institute: String,
    private val specialties: List<String>,
    private val specialGroups: List<String>,
    private val hasSpecialGroups: Boolean = false,
    private val savedPath: String,
) {

    private val distributionPreparation = DistributionPreparation(
        students = students,
        participations = participations
    )

    private val priorities = mutableMapOf<String, MutableList<Priority>>()
    //private val sortedPriorities = mapOf<String, MutableList<Priority>>()

    private var notApplied = mutableListOf<Student>()
    private var notAppliedStudents = setOf<Int>()
    var applied = 0

    private var participationIndex: Int = participations[participations.lastIndex].id + 1

    init {
        distributionPreparation.prepare()
        specialties.forEach {
            priorities[it] = mutableListOf()
        }
    }

    private fun preExecute() {
        distributeParticipations()
        findNotAppliedStudents()
        //if (hasSpecialGroups) distributeSpecialGroups()
    }

    fun execute() {
        preExecute()
        initPriorities()
        distributeSilentStudents()
        ExportDataToExcel.writeProjectsWithStudents(
            students = students,
            notApplied = notApplied,
            projects = projects,
            participations = participations,
            institute = institute,
            filePath = savedPath
        )
        logResults()
    }

//    fun execute() {
//        preExecute()
//        distributeSilentStudents()
//        distributeExcessStudents()
//        ExportDataToExcel.writeProjectsWithStudents(
//            students = students,
//            notApplied = notApplied,
//            projects = projects,
//            participations = participations,
//            institute = institute,
//            filePath = savedPath
//        )
//        logResults()
//    }
//
//    fun executeUniformly() {
//        preExecute()
//        distributeSilentStudents(isUniform = true)
//        distributeSilentStudentsToFullUniformly()
//        ExportDataToExcel.writeProjectsWithStudents(
//            students = students,
//            notApplied = notApplied,
//            projects = projects,
//            participations = participations,
//            institute = institute,
//            isUniformly = true,
//            filePath = savedPath
//        )
//        logResults()
//    }

    private fun distributeParticipations() {
        for (priority in (1..3)) {
            for (project in projects) {
                if (project.freePlaces != 0) {
                    val participationsForCurrentProject =
                        participations.filter {
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
                            val currentParticipationIndex = participations.indexOf(i)
                            participations[currentParticipationIndex].stateId = 1
                            applied++

                            val participationsToDelete =
                                participations.filter { it.studentId == participations[currentParticipationIndex].studentId && it.priority != priority }
                            for (j in participationsToDelete) {
                                participations[participations.indexOf(j)].stateId = 2
                            }
                            project.freePlaces--
                            project.busyPlaces++
                        }
                    }
                }
            }
        }
    }

    private fun findNotAppliedStudents() {
        val notAppliedParticipations = participations.filter { it.stateId == 0 }

        notAppliedStudents =
            participations.filter { it.stateId == 0 && students.map { stud -> stud.id }.contains(it.studentId) }
                .map { it.studentId }.toSet()

        notApplied.addAll(notAppliedStudents.map { students.find { stud -> stud.id == it }!! })

        notApplied.addAll(distributionPreparation.freeStudents)

        for (i in notAppliedParticipations.map { it.id }) {
            participations.find { it.id == i }!!.stateId = 2
        }
    }

//    private fun sortProjectList(): List<Project> {
//        val list = mutableListOf<Project>()
//
//        for (project in projects) {
//            if (project.freePlaces != 0) {
//                list.add(project)
//            }
//        }
//
//        val firstlySorted = list.sortedBy { it.freePlaces }
//        val highDemandProjects =
//            firstlySorted.filter { it.freePlaces <= ceil(PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY * PROJECT_LOWER_DEMAND_COEFFICIENT) }
//        val lowDemandProjects = firstlySorted.subtract(highDemandProjects.toSet()).toMutableList()
//        val toEnd = mutableListOf<Project>()
//
//        for (project in lowDemandProjects) {
//            if (highDemandProjects.find { it.supervisors[0] == project.supervisors[0] } != null) {
//                toEnd.add(project)
//            }
//        }
//        lowDemandProjects.removeAll(toEnd)
//        lowDemandProjects.addAll(toEnd)
//
//        return highDemandProjects + lowDemandProjects
//    }

    private fun initPriorities() {
        projects.forEach { project ->
            project.groups.forEach { group ->
                val koef = evaluateKoef(students, participations, group, project)
                if (koef != 0f) {
                    priorities[group]!!.add(
                        Priority(project.id, koef)
                    )
                }
            }
        }
    }

    private fun distributeSilentStudents(isUniformly: Boolean = true) {
        val sortedPriorities = priorities.toList().sortedBy { (key, value) -> value.size }.toMap()
        val freePlacesBoundary =
            if (isUniformly) PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY
            else 0

        sortedPriorities.forEach { entry ->
            val studentsForProjects = notApplied.filter { it.groupFamily == entry.key }.toMutableList()

            while (studentsForProjects.isNotEmpty() && sortedPriorities[entry.key]!!.isNotEmpty()) {
                val maxPriority = entry.value.maxBy { it.koef }
                val project = projects.find { maxPriority.projectId == it.id }!!

                if (project.freePlaces <= freePlacesBoundary) {
                    sortedPriorities[entry.key]!!.removeIf { it.projectId == project.id }
                    continue
                }

                val student = studentsForProjects[0]

                participations.add(
                    Participation(
                        id = participationIndex++,
                        priority = if (notAppliedStudents.contains(student.id)) 4 else 5,
                        projectId = maxPriority.projectId,
                        studentId = student.id,
                        stateId = 1
                    )
                )
                projects.find { it.id == maxPriority.projectId }!!.freePlaces--
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

        while (lowerValue < PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY && notApplied.isNotEmpty()) {
            sortedProjects = sortedProjects
                .sortedWith(compareBy({ it.busyPlaces }, { it.groups.size }))

            sortedProjects.forEach { project ->
                if (project.busyPlaces == lowerValue) {
                    val groupPriorities = mutableMapOf<String, Float>()
                    project.groups.forEach { group ->
                        groupPriorities[group] = priorities[group]!!.find { it.projectId == project.id }!!.koef
                    }

                    val groupToEnroll = groupPriorities.maxBy { (key, value) -> value }

                    try {
                        val student = students.first { it.groupFamily == groupToEnroll.key }

                        participations.add(
                            Participation(
                                id = participationIndex++,
                                priority = if (notAppliedStudents.contains(student.id)) 4 else 5,
                                projectId = project.id,
                                stateId = 1,
                                studentId = student.id
                            )
                        )

                        notApplied.removeIf { it.id == student.id }
                        updateKoef(priorities, groupToEnroll.key, project.id, students, participations, projects)
                        sortedProjects.find { it.id == project.id }!!.apply {
                            busyPlaces++
                            freePlaces--
                        }
                        lowerValue = sortedProjects.minBy { it.busyPlaces }.busyPlaces
                    } catch (_: NoSuchElementException) {}
                }
            }
        }
    }

//    private fun distributeSilentStudentsToFullUniformly() {
//        val projects = projects
//            .filter { !containsGroup(it, specialGroups) }
//            .sortedBy { it.freePlaces }
//            .reversed()
//
//        var areProjectsFull = false
//        var maxFreeStudentsCount = projects.maxOfOrNull { it.freePlaces }!!
//        println(maxFreeStudentsCount)
//
//        while (!areProjectsFull && notApplied.isNotEmpty()) {
//            var notAppliedSizeBefore = notApplied.size
//            for (project in projects) {
//                if (project.freePlaces == 0) continue
//                if (maxFreeStudentsCount <= 0) break
//                //if (project.freePlaces != maxFreeStudentsCount) continue
//
//                val bestMatchingStudent = findBestMatch(project = project)
//                //println("projectId = ${project.id} - $bestMatchingStudent")
//                if (bestMatchingStudent != null) {
//                    println(bestMatchingStudent)
//                    participations.add(
//                        Participation(
//                            id = participationIndex++,
//                            priority = if (notAppliedStudents.contains(bestMatchingStudent.id)) 4 else 5,
//                            projectId = project.id,
//                            studentId = bestMatchingStudent.id,
//                            stateId = 1
//                        )
//                    )
//                    notApplied.removeIf { it.id == bestMatchingStudent.id }
//                    projects[projects.indexOf(project)].freePlaces--
//                    maxFreeStudentsCount = projects.maxOfOrNull { it.freePlaces }!!
//                }
//            }
//            if (notAppliedSizeBefore == notApplied.size) {
//                areProjectsFull = true
//            }
//        }
//    }

//    private fun distributeSpecialGroups() {
//        val specialGroups = listOf<String>("ИИКб")
//        for (gr in specialGroups) {
//            val project = projects.find { it.groups.contains(gr) }!!
//            var bestMatchingStudent: Student? = findBestMatch(project = project)
//
//            while (bestMatchingStudent != null) {
//                participations.add(
//                    Participation(
//                        id = participationIndex++,
//                        priority = if (notAppliedStudents.contains(bestMatchingStudent.id)) 5 else 4,
//                        projectId = project.id,
//                        studentId = bestMatchingStudent.id,
//                        stateId = 1
//                    )
//                )
//                projects[projects.indexOfFirst { it.id == project.id }].freePlaces--
//
//                bestMatchingStudent = findBestMatch(project = project)
//            }
//        }
//    }

//    private fun distributeSilentStudents(isUniform: Boolean = false) {
//        for (project in sortProjectList()) {
//            val places: Int = if (isUniform) {
//                project.freePlaces - (PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY)
//            } else {
//                project.freePlaces
//                //project.freePlaces - (ru.student.distribution.PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - ru.student.distribution.PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY)
//            }
//            for (i in 0 until places) {
//                val bestMatchingStudent: Student? = findBestMatch(project = project)
//
//                if (bestMatchingStudent != null) {
//                    participations.add(
//                        Participation(
//                            id = participationIndex++,
//                            priority = if (notAppliedStudents.contains(bestMatchingStudent.id)) 4 else 5,
//                            projectId = project.id,
//                            studentId = bestMatchingStudent.id,
//                            stateId = 1
//                        )
//                    )
//                    projects[projects.indexOf(project)].freePlaces--
//                } else {
//                    break
//                }
//            }
//        }
//      }



//    private fun distributeSilentStudentsToFull() {
//        for (project in sortProjectList()) {
//            val places = PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - project.freePlaces
//            for (i in 0 until places) {
//                val bestMatchingStudent: Student? = findBestMatch(project = project)
//
//                if (bestMatchingStudent != null) {
//                    participations.add(
//                        Participation(
//                            id = participationIndex++,
//                            priority = 4,
//                            projectId = project.id,
//                            studentId = bestMatchingStudent.id,
//                            stateId = 1
//                        )
//                    )
//                    projects[projects.indexOfFirst { it.id == project.id }].freePlaces--
//                } else {
//                    break
//                }
//            }
//        }
//    }

//    private fun distributeExcessStudents() {
//        val excessProjects =
//            projects.filter { it.freePlaces > (PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - PROJECT_MIN_CAPACITY) && it.freePlaces != it.places }
//                .reversed()
//
//        val sortedProjects = projects.sortedBy { it.freePlaces }
//
//        for (project in excessProjects) {
//
//            val excessParticipations = participations.filter { it.projectId == project.id && it.stateId == 1 }
//
//            for (i in excessParticipations) {
//                val student = students.find { it.id == i.studentId }!!
//                val suitedProjects =
//                    sortedProjects.filter {
//
//                        it.id != project.id &&
//                                it.groups.contains(student.group) &&
//                                it.freePlaces <= (PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - PROJECT_MIN_CAPACITY)
//                    }
//
//                val suitedProject = suitedProjects.maxByOrNull { it.freePlaces }
//                if (suitedProject != null) {
//                    participations.remove(i)
//                    participations.add(
//                        Participation(
//                            id = participationIndex++,
//                            priority = 4,
//                            projectId = suitedProject.id,
//                            studentId = student.id,
//                            stateId = 1
//                        )
//                    )
//
//                    projects[projects.indexOf(project)].freePlaces++
//
//                    projects[projects.indexOf(suitedProject)].freePlaces--
//
//                }
//            }
//        }
//    }

//    private fun findBestMatch(project: Project): Student? {
//        var bestMatchingStudent: Student? = null
//        var special = project.groups.contains("ИИКб")
//
//        if (notApplied.isEmpty()) {
//            return null
//        }
//
//        var groupStudent: Student? = null
//
//        for (student in notApplied) {
//            if (special) {
//                if (student.group == "ИИКб") {
//                    bestMatchingStudent = student
//                    break
//                }
//            } else {
//                println("${project.groups} contains ${student.group} == ${project.groups.contains(student.group)}")
//                if (groupStudent == null && project.groups.contains(student.group)) {
//                    groupStudent = student
//                }
//            }
//        }
//
//        if (!special) bestMatchingStudent = groupStudent
//
//        notApplied.removeIf { it.id == bestMatchingStudent?.id }
//        return bestMatchingStudent
//    }

    private fun evaluateKoef(
        students: List<Student>,
        participation: List<Participation>,
        group: String,
        project: Project,
    ): Float {
        val groupStudents = students.count { it.groupFamily == group }
        val groupFreeStudents =
            students.count { it.groupFamily == group && participation.find { part -> part.studentId == it.id } == null }
        val koef = (project.freePlaces * 1.0f / project.places) * (groupFreeStudents * 1.0f / groupStudents)

        return koef
    }

    private fun updateKoef(
        map: Map<String, MutableList<Priority>>,
        group: String,
        projectId: Int,
        students: List<Student>,
        participation: List<Participation>,
        projects: List<Project>,
    ) {
        val project = projects.find { it.id == projectId }!!

        project.groups.forEach projectGroups@ {
            val koef = evaluateKoef(students, participation, it, project)

            if (koef == 0f) {
                map.forEach { entry ->
                    map[entry.key]!!.removeIf { item -> item.projectId == projectId }
                    return@projectGroups
                }
            } else {
                map[group]!!.find { it.projectId == project.id }!!.koef = koef
            }
        }

        map[group]!!.forEach {  priority ->
            if (priority.projectId != projectId) {
                val groupProject = projects.find { it.id == priority.projectId }!!
                val koef = evaluateKoef(students, participation, group, groupProject)

                if (koef == 0f) {
                    map.forEach { entry ->
                        map[entry.key]!!.removeIf { item -> item.projectId == projectId }
                    }
                } else {
                    map[group]!!.find { it.projectId == groupProject.id }!!.koef = koef
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