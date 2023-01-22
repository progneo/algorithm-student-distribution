package ru.student.distribution.data.model

import ru.student.distribution.domain.data.ImportCsvData

object Speciality {
    var specialities = ImportCsvData.getSpecialitiesFromFile("F:/yarmarka_data/new/specs.csv")

    init {
        println(specialities)
    }
}