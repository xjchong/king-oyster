package com.helloworldramen.kingoyster.architecture

sealed class Direction(val vector: Position) {
    object North : Direction(Position.NORTH)
    object East : Direction(Position.EAST)
    object South : Direction(Position.SOUTH)
    object West : Direction(Position.WEST)

    companion object {
        fun all() = listOf(North, East, South, West)
    }
}