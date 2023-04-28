//package ru.student.distribution
//
//import ru.student.distribution.data.model.*
//import ru.student.distribution.domain.distribution.Distribution
//import ru.student.distribution.domain.distribution.DistributionRule
//
//
///**
// * Example of launching algorithm
// */
//private fun main() {
//    val institutes = listOf(
//        Institute(
//            id = 0,
//            name = "1"
//        ),
//        Institute(
//            id = 1,
//            name = "2"
//        )
//    )
//    val departments = listOf(
//        Department(
//            id = 0,
//            name = "1.1",
//            institute = institutes[0]
//        ),
//        Department(
//            id = 1,
//            name = "1.2",
//            institute = institutes[0]
//        ),
//    )
//    val groups = listOf(
//        Specialty(
//            id = 0,
//            name = "ИСТб",
//            institute = institutes[0],
//            department = departments[0]
//        ),
//        Specialty(
//            id = 1,
//            name = "АСУб",
//            institute = institutes[0],
//            department = departments[1]
//        ),
//        Specialty(
//            id = 2,
//            name = "ЭВМ",
//            institute = institutes[0],
//            department = departments[1]
//        )
//    )
//    val students = mutableListOf<Student>()
//    (0..25).forEach {
//        val groupNumber = if (it < 15) 1 else 0
//        val group = groups[groupNumber]
//        val course = if (it < 15) 4 else 3
//        students.add(
//            Student(id = it, name = "Name $it", groupFamily = group.name, fullGroupName = group.name + "-$course", specialty = group, course = course)
//        )
//    }
//    val projects = mutableListOf<Project>(
//        Project(
//            id = 1,
//            title = "Project 1",
//            groups = listOf(groups[0], groups[1]),
//            places = 15,
//            freePlaces = 15,
//            busyPlaces = 0,
//            supervisors = listOf(),
//            difficulty = 1,
//            customer = "",
//            department = departments[0],
//            projectSpecialties = listOf(
//                ProjectSpecialty(
//                    id = 0,
//                    course = 3,
//                    specialty = groups[0],
//                    priority = 1
//                ),
//                ProjectSpecialty(
//                    id = 1,
//                    course = 4,
//                    specialty = groups[1],
//                    priority = 1
//                ),
//            )
//        ),
//        Project(
//            id = 2,
//            title = "Project 2",
//            groups = listOf(groups[1]),
//            places = 15,
//            freePlaces = 15,
//            busyPlaces = 0,
//            supervisors = listOf(),
//            difficulty = 1,
//            customer = "",
//            department = departments[0],
//            projectSpecialties = listOf(
//                ProjectSpecialty(
//                    id = 2,
//                    course = 4,
//                    specialty = groups[1],
//                    priority = 1
//                ),
//            )
//        ),
//    )
//    val participation = mutableListOf<Participation>()
//
//    participation.add(Participation(id = 0, priority = 1, projectId = 1, studentId = 15, stateId = 0))
////    participation.add(Participation(id = 1, priority = 1, projectId = 1, studentId = 16, stateId = 0))
////    participation.add(Participation(id = 2, priority = 2, projectId = 2, studentId = 0, stateId = 0))
////    participation.add(Participation(id = 3, priority = 1, projectId = 2, studentId = 1, stateId = 0))
////    participation.add(Participation(id = 4, priority = 1, projectId = 2, studentId = 2, stateId = 0))
//
//    Distribution(
//        students = students,
//        projects = projects,
//        participations = participation,
//        institute = institutes[0],
//        specialties = groups,
//        savingPath = "E:/yarmarka/",
//        distributionRule = DistributionRule(15, 9),
//        projectSpecialties = listOf()
//    ).execute()
//}
//
////private fun main() {
////    val groups = listOf("1", "2", "3")
////    val students = mutableListOf<Student>(
////        Student(1, "1", "1", "1",),
////        Student(2, "1", "1", "1",),
////        Student(3, "3", "3", "3",),
////        Student(4, "1", "1", "1",),
////        Student(5, "3", "3", "3",),
////        Student(6, "3", "3", "3",),
////        Student(7, "3", "3", "3",),
////        Student(8, "1", "1", "1",),
////        Student(9, "2", "2", "2",),
////    )
////    val projects = mutableListOf<Project>(
////        Project(
////            id = 1,
////            title = "Project 1",
////            groups = listOf("1", "2", "3"),
////            places = 3,
////            freePlaces = 3,
////            busyPlaces = 0,
////            supervisors = listOf("Supervisor 1"),
////            difficulty = 1,
////            customer = ""
////        ),
////        Project(
////            id = 2,
////            title = "Project 2",
////            groups = listOf("1", "2", "3"),
////            places = 3,
////            freePlaces = 3,
////            busyPlaces = 0,
////            supervisors = listOf("Supervisor 2"),
////            difficulty = 1,
////            customer = ""
////        ),
////        Project(
////            id = 3,
////            title = "Project 2",
////            groups = listOf("3"),
////            places = 3,
////            freePlaces = 3,
////            busyPlaces = 0,
////            supervisors = listOf("Supervisor 3"),
////            difficulty = 1,
////            customer = ""
////        ),
////    )
////    val participation = mutableListOf<Participation>()
////
////    participation.add(Participation(id = 0, priority = 1, projectId = 2, studentId = 1, stateId = 0))
////    participation.add(Participation(id = 1, priority = 1, projectId = 2, studentId = 3, stateId = 0))
////
////    val institute = "Institute"
////    val specialGroups = mutableListOf<String>()
////
////    Distribution(
////        students = students,
////        projects = projects,
////        participations = participation,
////        institute = institute,
////        specialties = groups,
////        specialGroups = specialGroups,
////        savedPath = "E:/yarmarka/",
////        distributionRule = DistributionRule(3, 0)
////    ).execute()
////}
