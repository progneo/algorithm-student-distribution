package ru.student.distribution.model

data class DistributionResults(
    val participation: List<Participation>,
    val instituteResults: List<InstituteResults>,
    val excessProjects: List<Project>
)
