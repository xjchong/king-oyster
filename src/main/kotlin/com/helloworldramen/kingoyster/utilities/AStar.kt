package com.helloworldramen.kingoyster.utilities

import java.util.*
import kotlin.Comparator
import kotlin.math.absoluteValue

private data class AStarPosition(val x: Int, val y: Int) {

    fun split(): Pair<Int, Int> {
        return Pair(x, y)
    }

    fun neighbors(): List<AStarPosition> {
        return listOf(
            AStarPosition(x, y - 1),
            AStarPosition(x + 1, y),
            AStarPosition(x, y + 1),
            AStarPosition(x - 1, y)
        )
    }
}

object AStar {

    val MANHATTAN_HEURISTIC: (from: Pair<Int, Int>, to: Pair<Int, Int>, cost: Double) -> Double = { from, to, cost ->
        ((from.first - to.first).absoluteValue + (from.second - to.second).absoluteValue).toDouble()
    }

    fun getPath(
        start: Pair<Int, Int>,
        goal: Pair<Int, Int>,
        cost: (from: Pair<Int, Int>, to: Pair<Int, Int>) -> Double,
        heuristic: (from: Pair<Int, Int>, to: Pair<Int, Int>, cost: Double) -> Double
    ): Iterable<Pair<Int, Int>> {
        return getPath(start.first, start.second, goal.first, goal.second, cost, heuristic)
    }

    fun getPath(
        startX: Int,
        startY: Int,
        goalX: Int,
        goalY: Int,
        cost: (from: Pair<Int, Int>, to: Pair<Int, Int>) -> Double,
        heuristic: (from: Pair<Int, Int>, to: Pair<Int, Int>, cost: Double) -> Double
    ): Iterable<Pair<Int, Int>> {
        val start = AStarPosition(startX, startY)
        val goal = AStarPosition(goalX, goalY)
        val frontier = PriorityQueue(FrontierEntry.comparator)
        val cameFrom = hashMapOf<AStarPosition, AStarPosition?>()
        val costSoFar = hashMapOf<AStarPosition, Double>()
        var currentPosition: AStarPosition = goal

        frontier.add(FrontierEntry(goal, 0.0))
        cameFrom[goal] = null
        costSoFar[goal] = 0.0

        while (frontier.isNotEmpty()) {
            currentPosition = frontier.remove().position

            if (currentPosition == start) break

            for (nextPosition in currentPosition.neighbors().filter {
                costSoFar[it] == null
            }) {
                val currentCost = costSoFar[currentPosition] ?: continue
                val nextCost = cost(currentPosition.split(), nextPosition.split())
                val newCost = currentCost + nextCost + heuristic(nextPosition.split(), start.split(), nextCost)

                if (!costSoFar.contains(nextPosition) || newCost < currentCost) {
                    costSoFar[nextPosition] = newCost
                    frontier.add(FrontierEntry(nextPosition, newCost))
                    cameFrom[nextPosition] = currentPosition
                }
            }
        }

        val path = mutableListOf<AStarPosition>()

        currentPosition = cameFrom[currentPosition] ?: goal
        while (currentPosition != goal) {
            path.add(currentPosition)
            currentPosition = cameFrom[currentPosition] ?: goal
        }
        path.add(goal)

        return path.map { it.split() }
    }

    private data class FrontierEntry(val position: AStarPosition, val priority: Double) {

        companion object {
            val comparator: Comparator<FrontierEntry> = compareBy { it.priority }
        }
    }
}