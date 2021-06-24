package com.helloworldramen.kingoyster.utilities

object FloodFill {

    private data class FloodFillPosition(val x: Int, val y: Int) {

        fun withRelativeX(deltaX: Int): FloodFillPosition {
            return copy(x = x + deltaX)
        }

        fun withRelativeY(deltaY: Int): FloodFillPosition {
            return copy(y = y + deltaY)
        }

        fun neighbors(): List<FloodFillPosition> {
            return listOf(
                withRelativeX(1),
                withRelativeX(-1),
                withRelativeY(1),
                withRelativeY(-1),
            )
        }
    }

    fun fill(originX: Int, originY: Int, maxDepth: Int,
             isBlocked: (x: Int, y: Int) -> Boolean): List<Pair<Int, Int>> {
        val origin = FloodFillPosition(originX, originY)

        return fillRecursive(origin, mutableSetOf(), maxDepth, isBlocked).map {
            Pair(it.x, it.y)
        }
    }

    private fun fillRecursive(
        position: FloodFillPosition,
        filled: MutableSet<FloodFillPosition>,
        remainingDepth: Int,
        isBlocked: (x: Int, y: Int) -> Boolean
    ): Set<FloodFillPosition> {
        if (remainingDepth <= 0) return filled

        filled.add(position)

        for (neighbor in position.neighbors()) {
            if (isBlocked(neighbor.x, neighbor.y)) continue

            fillRecursive(neighbor, filled, remainingDepth - 1, isBlocked)
        }

        return filled
    }
}