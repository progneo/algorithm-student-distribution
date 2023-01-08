package data.generation

import data.model.State

object States {
    val states = listOf<State>(
        State(0, "WAITING"),
        State(1, "APPLIED"),
        State(2, "CANCELED"),
    )
}