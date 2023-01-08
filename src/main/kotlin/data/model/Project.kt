package data.model

import PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY
import PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY

data class Project(
    var id: Int = 0,
    var title: String = "",
    var places: Int = 0,
    var freePlaces: Int = PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY,
    var groups: List<String> = emptyList(),
    var goal: String = "",
    var description: String = "",
    var difficulty: Int = 0,
    var date_start: String = "",
    var date_end: String = "",
    var requirements: String = "",
    var customer: String = "",
    var study_result: String = "",
    var product_result: String = "",
    var supervisors: List<String> = emptyList(),
    var skills: List<String> = emptyList()
)