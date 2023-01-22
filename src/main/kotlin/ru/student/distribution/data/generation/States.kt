package ru.student.distribution.data.generation

import ru.student.distribution.data.model.State

object States {
    val states = listOf<State>(
        State(0, "WAITING"),
        State(1, "APPLIED"),
        State(2, "CANCELED"),
    )
}