package ru.student.distribution.domain.data


import ru.student.distribution.data.model.Project
import ru.student.distribution.data.model.Student
import ru.student.distribution.data.model.Supervisor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

class ProjectData {
    var id: String = ""
}

object ImportExcelData {

//    fun getProjectsFromDir(filePath: String): List<Project> {
//        val projects = mutableListOf<Project>()
//        File(filePath).walk().forEach {
//            if (!it.isDirectory) {
//                projects.addAll(getProjectsFromFile(it.absolutePath))
//            }
//        }
//        return projects
//    }

//    fun getProjectsFromFile(filePath: String): List<Project> {
//        val wb = XSSFWorkbook(FileInputStream(File(filePath)))
//
//        val sheet = wb.getSheetAt(0)
//
//        val list = mutableListOf<Project>()
//        var supervisorIndex = 0
//        for ((index, i) in (1..sheet.lastRowNum).withIndex()) {
//            val row = sheet.getRow(i)
//
//            val project = Project()
//
//            project.id = index
//            project.title = row.getCell(1).stringCellValue
//            project.places = PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY
//
//            val additionalGroups = if (row.getCell(7).stringCellValue.isNotEmpty()) {
//                ", " + row.getCell(7).stringCellValue
//            } else ""
//            project.groups = (row.getCell(6).stringCellValue + additionalGroups)
//                .split(" ")
//                .filter { GenerateProjects.groups.contains(it) }
//
//            project.goal = row.getCell(3).stringCellValue
//            project.description = row.getCell(4).stringCellValue
//            project.difficulty = row.getCell(9).numericCellValue.toInt()
//            project.date_start = row.getCell(1).stringCellValue
//            project.date_end = row.getCell(1).stringCellValue
//            project.requirements = row.getCell(8).stringCellValue
//            project.customer = row.getCell(2).stringCellValue
//            project.study_result = row.getCell(11).stringCellValue
//            project.product_result = row.getCell(10).stringCellValue
//            project.supervisors = row.getCell(5).stringCellValue.split(",")
//
//            for (supervisor in project.supervisors) {
//                GenerateProjects.supervisors.add(
//                    Supervisor(
//                        id = supervisorIndex++,
//                        name = supervisor
//                    )
//                )
//            }
//
//            list.add(project)
//        }
//        return list
//    }

//    fun getStudentsFromFile(filePath: String): List<Student> {
//        val wb = XSSFWorkbook(FileInputStream(File(filePath)))
//
//        val sheet = wb.getSheetAt(0)
//
//        val list = mutableListOf<Student>()
//        for (i in 1..sheet.lastRowNum) {
//            val row = sheet.getRow(i)
//
//            val student = Student()
//
//            student.id = row.getCell(5).numericCellValue.toInt()
//            student.name = row.getCell(3).stringCellValue
//            val group = row.getCell(6).stringCellValue
//            student.group = group.substring(0, group.indexOfFirst { it == '-' })
//            student.realGroup = row.getCell(6).stringCellValue
//
//            if (!student.group.endsWith("з")) {
//                list.add(student)
//            }
//        }
//        return list
//    }

//    fun getStudentsFromFile(filePath: String): List<Student> {
//        val wb = XSSFWorkbook(FileInputStream(File(filePath)))
//
//        val sheet = wb.getSheetAt(0)
//
//        val list = mutableSetOf<Student>()
//        for (i in 1..sheet.lastRowNum) {
//            val row = sheet.getRow(i)
//
//            val student = Student()
//
//            student.numz = row.getCell(2).numericCellValue.toInt()
//            student.name = row.getCell(0).stringCellValue
//            student.group = row.getCell(1).stringCellValue
//            student.projectId = row.getCell(3).numericCellValue.toInt()
//            student.projectName = row.getCell(4).stringCellValue
//
//            list.add(student)
//        }
//        return list.toList()
//    }

//    fun getStudentsFromFile(filePath: String, exceptionsFilePath: String): Pair<List<Student>, List<Student>> {
//        val wb = XSSFWorkbook(FileInputStream(File(filePath)))
//        val wbException = XSSFWorkbook(FileInputStream(File(exceptionsFilePath)))
//
//        val sheet = wb.getSheetAt(0)
//        val sheetException = wbException.getSheetAt(0)
//
//        val exceptionList = mutableListOf<Student>()
//        for (i in 1..sheetException.lastRowNum) {
//            val row = sheetException.getRow(i)
//
//            val student = Student()
//
//            student.name = row.getCell(0).stringCellValue
//            val group = row.getCell(1).stringCellValue
//                .replace(" ", "")
//            //println(group)
//            student.group = group.substring(0, group.indexOfFirst { it == '-' })
//
//            //println(student)
//            exceptionList.add(student)
//        }
//
//        val list = mutableListOf<Student>()
//        for (i in 2..sheet.lastRowNum) {
//            val row = sheet.getRow(i)
//
//            val role = row.getCell(4).stringCellValue
//            val group = row.getCell(6).stringCellValue
//            val institute = row.getCell(8).stringCellValue
//            if (role != "студент" ||
//                institute.contains("БРИКС") ||
//                (!group.contains("19") && !group.contains("20"))
//            ) {
//                continue
//            }
//
//            val student = Student()
//
//            student.id = row.getCell(5).numericCellValue.toInt()
//            student.name = row.getCell(3).stringCellValue
//            student.group = group.substring(0, group.indexOfFirst { it == '-' })
//            student.realGroup = group
//
//            if (exceptionList.find { it.name == student.name && it.group.lowercase() == student.group.lowercase() } != null) {
//                val index = exceptionList.indexOfFirst { it.name == student.name && it.group.lowercase() == student.group.lowercase() }
//                exceptionList[index].id = student.id
//
//                continue
//            }
//
//
//            list.add(student)
//        }
//        return Pair(list, exceptionList)
//    }
}