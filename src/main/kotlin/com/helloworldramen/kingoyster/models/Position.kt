package com.helloworldramen.kingoyster.models

data class Position(val x: Int = 0, val y: Int = 0) {

    fun withX(x: Int): Position {
        return if (x == this.x) this else copy(x = x)
    }

    fun withY(y: Int): Position {
        return if (y == this.y) this else copy(y = y)
    }

    fun withRelativeX(delta: Int): Position {
        return if (delta == 0) this else copy(x = x + delta)
    }

    fun withRelativeY(delta: Int): Position {
        return if (delta == 0) this else copy(y = y + delta)
    }

    fun withRelative(deltaX: Int, deltaY: Int): Position {
        return if (deltaX == 0 && deltaY == 0) this else copy(x = x + deltaX, y = y + deltaY)
    }

    fun north(): Position = withRelativeY(-1)

    fun east(): Position = withRelativeX(1)

    fun south(): Position = withRelativeY(1)

    fun west(): Position = withRelativeX(-1)

    fun neighbors(): List<Position> = listOf(north(), east(), south(), west())

    fun neighborsShuffled(): List<Position> = neighbors().shuffled()

    fun forEach(action: (Position) -> Unit) {
        (0..y).forEach { _y ->
            (0..x).forEach { _x ->
                action(Position(_x, _y))
            }
        }
    }

    fun <R> map(transform: (Position) -> R): List<R> {
        return mutableListOf<R>().apply {
            this@Position.forEach { add(transform(it)) }
        }
    }
}
