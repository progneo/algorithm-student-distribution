package data.generation

import STUDENT_MEAN_SKILL_COUNT
import STUDENT_WITH_NULL_SKILLS_CHANCE
import data.model.Skill

object GenerateSkills {

    private const val IST = "ИСТ"
    private const val ISM = "ИСМ"
    private const val ASU = "АСУ"
    private const val EVM = "ЭВМ"

    private val groups = listOf<String>(
        IST,
        ISM,
        ASU,
        EVM
    )

    private val _skills = listOf<String>(
        "web",
        "android",
        "ios",
        "machine learning",
        "flutter",
        "desktop",
        "analysis",
        "modeling",
        "state writing",
        "painting",
        "UI/UX"
    )

    private val skills = mutableListOf<Skill>()

    private lateinit var IST_SKILLS: List<Skill>
    private lateinit var ISM_SKILLS: List<Skill>
    private lateinit var ASU_SKILLS: List<Skill>
    private lateinit var EVM_SKILLS: List<Skill>

    var groupSkills: Map<String, List<Skill>>

    init {
        for (i in 0 until (_skills.size)) {
            skills.add(
                Skill(
                    id = i,
                    skill = _skills[i]
                )
            )
        }

        IST_SKILLS = listOf<Skill>(
            skills[0],
            skills[1],
            skills[2]
        )
        ISM_SKILLS = listOf<Skill>(
            skills[0],
            skills[1],
            skills[3]
        )
        ASU_SKILLS = listOf<Skill>(
            skills[0],
            skills[4],
            skills[5]
        )
        EVM_SKILLS = listOf<Skill>(
            skills[0],
            skills[5],
            skills[6]
        )

        groupSkills = mutableMapOf<String, List<Skill>>(
            IST to IST_SKILLS,
            ISM to ISM_SKILLS,
            ASU to ASU_SKILLS,
            EVM to EVM_SKILLS
        )
    }


    fun generateGroup(): String {
        return groups.random()
    }

    fun generateGroups(count: Int): List<String> {
        val generatedGroups = mutableSetOf<String>()

        for (i in 1 .. count) {
            var group = generateGroup()
            while (generatedGroups.contains(group)) {
                group = generateGroup()
            }
            generatedGroups.add(group)
        }

        return generatedGroups.toList()
    }

    fun getRandomSkill() = skills[skills.indices.random()]

    fun generateSkills(): List<Skill> {
        val resultSkills = mutableSetOf<Skill>()

        if (STUDENT_WITH_NULL_SKILLS_CHANCE.random() == 0) {
            return emptyList()
        }

        for (i in 0..STUDENT_MEAN_SKILL_COUNT.random()) {
            resultSkills.add(
                skills[(skills.indices).random()]
            )
        }

        return resultSkills.toList()
    }
}