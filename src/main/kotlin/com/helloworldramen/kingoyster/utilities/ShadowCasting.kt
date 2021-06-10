package com.helloworldramen.kingoyster.utilities

import kotlin.math.ceil
import kotlin.math.floor


private data class ShadowCastPosition(val x: Int, val y: Int) {
    fun withRelativeX(delta: Int): ShadowCastPosition {
        return copy(x = x + delta)
    }

    fun withRelativeY(delta: Int): ShadowCastPosition {
        return copy(y = y + delta)
    }
}


/**
 * A [Quadrant] represents a 90 degree sector pointing north, south, east, or west.
 * [Quadrant]s are traversed row by row. For the east and west [Quadrant]s, these
 * "rows" are vertical, not horizontal.
 */
private class Quadrant(private val cardinal: Int, private val origin: ShadowCastPosition) {

    companion object {
        const val NORTH = 0
        const val EAST = 1
        const val SOUTH = 2
        const val WEST = 3
    }

    /**
     * Convert a [ShadowCastPosition] relative to the current quadrant to a [ShadowCastPosition]
     * representing an absolute position in the grid.
     */
    fun transform(position: ShadowCastPosition): ShadowCastPosition {
        val (row, col) = position
        return when (cardinal) {
            NORTH -> origin.withRelativeX(col).withRelativeY(-row)
            SOUTH -> origin.withRelativeX(col).withRelativeY(row)
            EAST -> origin.withRelativeX(row).withRelativeY(col)
            else -> origin.withRelativeX(-row).withRelativeY(col)
        }
    }
}

/**
 * A [Row] represents a segment of [ShadowCastPosition]s bound between a start and end slope.
 * [depth] represents the distance between the row and the [Quadrant]'s origin.
 */
private class Row(val depth: Int, var startSlope: Double, var endSlope: Double) {

    /**
     * Returns an iterator over the tiles in the row
     */
    fun positions(): Iterator<ShadowCastPosition> {
        val minCol = roundTiesUp(depth * startSlope)
        val maxCol = roundTiesDown(depth * endSlope)

        return (minCol.toInt()..maxCol.toInt()).map {
            ShadowCastPosition(depth, it)
        }.iterator()
    }

    fun next(): Row {
        return Row(depth + 1, startSlope, endSlope)
    }

    private fun roundTiesUp(number: Double): Double {
        return floor(number + 0.5)
    }

    private fun roundTiesDown(number: Double): Double {
        return ceil(number - 0.5)
    }
}

/**
 * Shadow casting FOV implementation translated from
 * [Albert Ford's python version](https://www.albertford.com/shadowcasting).
 */
object ShadowCasting {

    /**
     * Computes the field of view from an origin tile using a recursive implementation for symmetric shadow casting.
     *
     * @param originX      The x value of the position from which to calculate the FOV
     * @param originX      The y value of the position from which to calculate the FOV
     * @param radius      The depth of the FOV from the origin
     * @param isBlocking  A function that returns true if a position blocks FOV, false otherwise
     * @param markVisible Positions that are determined to be in the FOV will be passed to this function
     */
    fun computeFOV(originX: Int, originY: Int, radius: Int,
                   isBlocking: (x: Int, y: Int) -> Boolean,
                   markVisible: (x: Int, y: Int) -> Unit) {
        /**
         * Within [computeFOV], we define some local functions that abstract away the details of [Quadrant]s
         * from the `scan` function. The positions passed to `reveal` `isOpaque` and `isOpen` are relative to the
         * current [Quadrant]. In contrast, the positions for `isBlocking` and `markVisible` are absolute positions.
         */
        val origin = ShadowCastPosition(originX, originY)
        markVisible(originX, originY)

        repeat(4) { i ->
            val quadrant = Quadrant(i, origin)

            fun reveal(position: ShadowCastPosition) {
                val (x, y) = quadrant.transform(position)
                markVisible(x, y)
            }

            fun isOpaque(position: ShadowCastPosition?): Boolean {
                return if (position != null) {
                    val (x, y) = quadrant.transform(position)
                    isBlocking(x, y)
                } else false
            }

            fun isOpen(position: ShadowCastPosition?): Boolean {
                return if (position != null) {
                    val (x, y) = quadrant.transform(position)
                    isBlocking(x, y).not()
                } else false
            }

            fun scan(row: Row) {
                if (row.depth >= radius) return

                var previousPosition: ShadowCastPosition? = null

                for (position in row.positions()) {
                    if (isOpaque(position) || isSymmetric(row, position)) {
                        reveal(position)
                    }

                    if (isOpaque(previousPosition) && isOpen(position)) {
                        row.startSlope = getSlope(position)
                    }

                    if (isOpen(previousPosition) && isOpaque(position)) {
                        val nextRow = row.next()
                        nextRow.endSlope = getSlope(position)
                        scan(nextRow)
                    }

                    previousPosition = position
                }

                if (isOpen(previousPosition)) {
                    scan(row.next())
                }
            }

            val firstRow = Row(1, -1.0, 1.0)
            scan(firstRow)
        }
    }

    private fun isSymmetric(row: Row, position: ShadowCastPosition): Boolean {
        val (_, y) = position

        return (y >= row.depth * row.startSlope
                && y <= row.depth * row.endSlope)
    }

    private fun getSlope(position: ShadowCastPosition): Double {
        val rowDepth = position.x.toDouble()
        val col = position.y.toDouble()
        val dividend = (2 * col - 1)
        val divisor = (2 * rowDepth)

        return dividend / divisor
    }
}