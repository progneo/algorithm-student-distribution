package data.model

import domain.data.ImportCsvData

object Speciality {
    var specialities = ImportCsvData.getSpecialitiesFromFile("F:/yarmarka_data/new/specs.csv")

    init {
        println(specialities)
    }
}