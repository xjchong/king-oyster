package com.helloworldramen.kingoyster.architecture

import kotlin.math.*

data class Position(val x: Int = 0, val y: Int = 0) {

    constructor(xy: Pair<Int, Int>): this(xy.first, xy.second)

    operator fun plus(position: Position): Position {
        return Position(this.x + position.x, this.y + position.y)
    }

    operator fun minus(position: Position): Position {
        return Position(this.x - position.x, this.y - position.y)
    }

    operator fun times(scalar: Double): Position {
        return Position((this.x * scalar).roundToInt(), (this.y * scalar).roundToInt())
    }

    operator fun times(scalar: Int): Position {
        return Position(this.x * scalar, this.y * scalar)
    }

    fun asPair(): Pair<Int, Int> {
        return Pair(x, y)
    }

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

    fun withRelative(delta: Position): Position = withRelative(delta.x, delta.y)

    fun north(): Position = withRelativeY(-1)

    fun east(): Position = withRelativeX(1)

    fun south(): Position = withRelativeY(1)

    fun west(): Position = withRelativeX(-1)

    fun rotated(origin: Position, direction: Direction,
                   originalDirection: Direction = Direction.South): Position {
        if (direction == originalDirection) return this

        val clockwiseDirections = listOf(Direction.North, Direction.East, Direction.South, Direction.West)
        val normalizedPosition = this - origin
        val rotationDelta = (clockwiseDirections.indexOf(direction) - clockwiseDirections.indexOf(originalDirection) + 4) % 4

        val normalizedRotatedPosition = when (rotationDelta) {
            1 -> Position(-normalizedPosition.y, normalizedPosition.x) // Clockwise 1/4
            2 -> Position(-normalizedPosition.x, -normalizedPosition.y) // Clockwise 2/4
            3 -> Position(normalizedPosition.y, -normalizedPosition.x) // Clockwise 3/4 (CCW 1/4)
            else -> normalizedPosition // No change (should already be handled).
        }

        return normalizedRotatedPosition + origin
    }

    fun neighbors(): List<Position> = listOf(north(), east(), south(), west())

    fun neighborsShuffled(): List<Position> = neighbors().shuffled()

    fun diagonalNeighbors(): List<Position> = listOf(
        this.withRelative(1, -1),
        this.withRelative(1, 1),
        this.withRelative(-1, 1),
        this.withRelative(-1, -1)
    )

    fun distanceFrom(position: Position): Double {
        return sqrt((x - position.x).toDouble().pow(2) + (y - position.y).toDouble().pow(2))
    }

    fun range(otherPosition: Position): List<Position> {
        val positions: MutableList<Position> = mutableListOf()

        forRange(otherPosition) { positions.add(it) }

        return positions
    }

    fun forRange(otherPosition: Position, action: (Position) -> Unit) {
        val minX = min(x, otherPosition.x)
        val maxX = max(x, otherPosition.x)
        val minY = min(y, otherPosition.y)
        val maxY = max(y, otherPosition.y)

        (minY..maxY).forEach { _y ->
            (minX..maxX).forEach { _x ->
                action(Position(_x, _y))
            }
        }
    }

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

    fun toList(): List<Position> {
        return map { it }
    }

    companion object {
        val NORTH = Position(0, -1)
        val EAST = Position(1, 0)
        val SOUTH = Position(0, 1)
        val WEST = Position(-1, 0)
    }
}

