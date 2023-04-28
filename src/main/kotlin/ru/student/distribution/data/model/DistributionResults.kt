package ru.student.distribution.data.model

data class DistributionResults(
    val allParticipation: List<Participation>,
    val institutesResults: List<InstituteResults>
)
