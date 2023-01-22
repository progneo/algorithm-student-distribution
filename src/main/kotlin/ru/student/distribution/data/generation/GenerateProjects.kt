package ru.student.distribution.data.generation

import ru.student.distribution.data.model.Supervisor

object GenerateProjects {

    val supervisors = mutableListOf<Supervisor>()
    val groups = listOf<String>("ЭВМб", "ИСТб", "ИСМб", "АСУб")



//    fun generateProjects(): List<Project> {
//        val projects = mutableListOf<Project>()
//        val supervisors = GenerateSupervisors.generateSupervisors().toMutableList()
//        val map = mutableMapOf<Int, Int>()
//        for (supervisor in supervisors) {
//            map[supervisor.id] = 0
//        }
//
//        for (i in 0 until ru.student.distribution.PROJECTS_COUNT) {
//            var supervisor = supervisors[(supervisors.indices).random()]
//            while (map[supervisor.id] == 2) {
//                supervisor = supervisors[(supervisors.indices).random()]
//            }
//
//            val index = map[supervisor.id]!!
//            map[supervisor.id] = index + 1
//
//            if (map[supervisor.id] == 2) {
//                map.remove(supervisor.id)
//                supervisors.remove(supervisor)
//            }
//
//            val project = Project(
//                id = i,
//                title = "$i",
//                placesLower = ru.student.distribution.PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY,
//                placesUpper = ru.student.distribution.PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY,
//                freePlaces = ru.student.distribution.PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY,
//                groups = GenerateSkills.generateGroups(count = ru.student.distribution.getPROJECT_GROUPS_COUNT.random()),
//                supervisor_name = supervisor.fio
//            )
//            projects.add(project)
//        }
//
//        return projects
//    }
//
//    fun generateProjectSkills(projects: List<Project>): List<ProjectSkills> {
//        val list = mutableListOf<ProjectSkills>()
//
//        for (project in projects) {
//            val skills = mutableSetOf<Skill>()
//            for (i in 0..ru.student.distribution.getPROJECT_MEAN_SKILL_COUNT.random()) {
//                val skill = GenerateSkills.getRandomSkill()
//                if (!skills.contains(skill)) {
//                    list.add(
//                        ProjectSkills(
//                            projectId = project.id,
//                            skill = skill
//                        )
//                    )
//                }
//                skills.add(skill)
//            }
//        }
//
//        return list
//    }
}