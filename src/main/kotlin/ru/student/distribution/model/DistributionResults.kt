package ru.student.distribution.model

data class DistributionResults(
    val allParticipation: List<Participation>,
    val institutesResults: List<InstituteResults>
)
