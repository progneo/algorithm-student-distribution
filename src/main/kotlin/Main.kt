import data.model.Participation
import data.model.Project
import data.model.Speciality
import domain.data.ImportCsvData
import domain.data.ImportExcelData
import domain.distribution.Distribution
import util.containsGroup

fun main() {
    val students = ImportExcelData.getStudentsFromFile("F:/yarmarka_data/stud_new_new.xlsx", "F:/yarmarka_data/stud_exception.xlsx")
    val projects = ImportCsvData.getProjectsFromFile("F:/yarmarka_data/new/projects.csv")
    val participations = ImportCsvData.getParticipationsFromFile("F:/yarmarka_data/new/participations.csv", students.second)
    val specialities = Speciality.specialities
    val specialGroups = listOf(
        "ИИКб"
    )
    participations.forEach {
        if (students.second.map { it.id }.contains(it.studentId)) {
            println("ALERT ${it.studentId}")
        }
    }

    for (institute in specialities.keys) {
        if (institute == "Байкальский институт БРИКС") continue

        val specs = specialities[institute]!!

        val studs = students.first.toMutableList()
            .filter {
                specs.contains(it.training_group)
            }
            .toMutableList()
        val projs = projects
            .filter {
                containsGroup(it, specs) && it.id != 240
            }
            .toMutableList()

        val parts = participations
            .filter {
                containsGroup(projects.find { proj -> proj.id == it.projectId }!!, specs)
            }
            .toMutableList()

        if (parts.size == 0) continue
        val dis = Distribution(
            students = studs,
            projects = projs,
            participations = parts,
            institute = institute,
            specialities = specs,
            specialGroups = specialGroups,
            hasSpecialGroups = institute == "Институт информационных технологий и анализа данных"
        )
        dis.executeUniformly()
    }
}