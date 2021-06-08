package com.helloworldramen.kingoyster.worldgen.topology

import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.utilities.Probability
import com.helloworldramen.kingoyster.utilities.percentChance
import java.util.*
import kotlin.math.roundToInt

class DungeonTopology(
    private val roomAttemptsPercent: Double = 0.2,
    private val roomMeanWidthPercent: Double = 0.25,
    private val roomMeanHeightPercent: Double = 0.25,
    private val roomWidthStandardDeviationPercent: Double = 0.0,
    private val roomHeightStandardDeviationPercent: Double = 0.0,
    private val cyclePercent: Probability = 5.percentChance(),
    private val extraDoorPercent: Probability = 5.percentChance(),
    private val maxExtraDoorsPercent: Probability = 4.percentChance(),
    private val pillarRemovalPercent: Probability = 50.percentChance(),
    private val shouldRemoveDeadEnds: Boolean = true
): TopologyStrategy() {

    private var regionIds: MutableMap<Position, Int> = mutableMapOf()
    private var nextRegionId: Int = 0
    private var mergedRegionIds: MutableSet<MutableSet<Int>> = mutableSetOf()

    override fun generateTopologyMap(width: Int, height: Int, playerPosition: Position?): TopologyMap {
        return TopologyMap(width, height).apply {
            fill(WALL_REGION_ID, TopologyType.Wall)

            placeRooms(
                (area() * roomAttemptsPercent).roundToInt(),
                (width * roomMeanWidthPercent).roundToInt(),
                (height * roomMeanHeightPercent).roundToInt(),
                (width * roomWidthStandardDeviationPercent),
                (height * roomHeightStandardDeviationPercent))

            placeCorridors(cyclePercent.double)

            placeDoors(
                extraDoorPercent.double,
                (area() * maxExtraDoorsPercent.double).roundToInt())

            removePillars(pillarRemovalPercent.double)

            if (shouldRemoveDeadEnds) removeDeadEnds()
        }
    }

    private fun getOdd(number: Int): Int {
        return if (number % 2 == 0) number + 1 else number
    }

    private fun nextGaussian(mu: Int, standardDeviation: Double): Double {
        return (Random().nextGaussian() * standardDeviation) + mu
    }

    private fun TopologyMap.fill(regionId: Int, type: TopologyType) {
        Position(width - 1, height - 1).forEach {
            set(it, type)
            regionIds[it] = regionId
        }
    }

    private fun TopologyMap.placeRooms(attempts: Int, meanWidth: Int, meanHeight: Int,
                                       widthStandardDeviation: Double, heightStandardDeviation: Double) {
        repeat(attempts) {
            val x = getOdd((Math.random() * width).roundToInt())
            val y = getOdd((Math.random() * height).roundToInt())
            val roomWidth = getOdd(nextGaussian(meanWidth, widthStandardDeviation).roundToInt())
            val roomHeight = getOdd(nextGaussian(meanHeight, heightStandardDeviation).roundToInt())

            if (isRoomSafe(x, y, roomWidth, roomHeight)) {
                placeRoom(x, y, roomWidth, roomHeight)
                nextRegionId++
            }
        }
    }

    private fun TopologyMap.placeRoom(x: Int, y: Int, roomWidth: Int, roomHeight: Int) {
        val origin = Position(x, y)

        origin.forRange(origin.withRelative(roomWidth - 1, roomHeight - 1)) {
            set(it, TopologyType.Floor)
            regionIds[it] = nextRegionId
        }
    }

    private fun TopologyMap.isRoomSafe(x: Int, y: Int, roomWidth: Int, roomHeight: Int): Boolean {
        val origin = Position(x, y)
        if (origin.isOutOfBounds(this)) return false
        if (roomWidth < 2 || roomHeight < 2) return false // Room is too small.
        if ((x + roomWidth >= width) || (y + roomHeight >= height)) return false

        for (pos in origin.range(Position(x + roomWidth - 1, y + roomHeight- 1))) {
            if (!isWall(pos)) return false
        }

        return true
    }

    private fun TopologyMap.placeCorridors(cyclePercent: Double) {
        for (x in (1 until width - 1) step 2) {
            for (y in (1 until height - 1) step 2) {
                val pos = Position(x, y)
                if (isWall(pos)) {
                    placeCorridorFrom(pos, cyclePercent)
                    nextRegionId++
                }
            }
        }
    }

    private fun TopologyMap.placeCorridorFrom(startPos: Position, cyclePercent: Double) {
        val directions = mutableListOf('e', 's', 'w', 'n').shuffled()
        set(startPos, TopologyType.Floor)
        regionIds[startPos] = nextRegionId

        for (direction in directions) {
            val endPos = when (direction) {
                'e' -> startPos.withRelativeX(2)
                's' -> startPos.withRelativeY(2)
                'w' -> startPos.withRelativeX(-2)
                else -> startPos.withRelativeY(-2)
            }

            val couldCreateCycle = regionIds[endPos] == nextRegionId && Math.random() < cyclePercent

            if (isWall(endPos) && !endPos.isOutOfBounds(this) || couldCreateCycle) {
                startPos.forRange(endPos) { pos ->
                    set(pos, TopologyType.Floor)
                    regionIds[pos] = nextRegionId
                }

                placeCorridorFrom(endPos, cyclePercent)
            }
        }
    }

    private fun TopologyMap.placeDoors(extraDoorPercent: Double, maxExtraDoors: Int) {
        val positions = getAllWallPositions() // Only walls can be connectors, so iterate through them randomly.
        var extraDoorsCount = 0

        for (pos in positions) {
            val adjacentRegions: MutableSet<Int> = mutableSetOf()

            for (neighbor in pos.neighbors()) {
                if (isWallOrDoor(neighbor)) continue

                val neighbourId = regionIds[neighbor] ?: continue
                adjacentRegions.add(neighbourId)
            }

            if (adjacentRegions.size != 2) continue // Does not connect two different regions.

            var needsMerge = true
            var isDisjoint = true
            val canCreateExtra = extraDoorsCount < maxExtraDoors && Math.random() < extraDoorPercent

            for (mergedSet in mergedRegionIds) {
                val intersectionSize = mergedSet.intersect(adjacentRegions).size

                if (intersectionSize == 2) needsMerge = false
                if (intersectionSize == 1) {
                    isDisjoint = false
                    mergedSet.addAll(adjacentRegions)
                }
            }

            if (needsMerge) {
                set(pos, TopologyType.Door)
                if (isDisjoint) mergedRegionIds.add(adjacentRegions)
            } else if (canCreateExtra) {
                set(pos, TopologyType.Door)
                extraDoorsCount++
            }
        }
    }

    private fun TopologyMap.removeDeadEnds() {
        Position(width - 1, height - 1).forEach {
            removeDeadEnd(it)
        }
    }

    private fun TopologyMap.removeDeadEnd(position: Position) {
        if (!isDeadEnd(position)) return

        set(position, TopologyType.Wall)
        regionIds[position] = WALL_REGION_ID

        position.neighbors().forEach {
            removeDeadEnd(it)
        }
    }

    private fun TopologyMap.isDeadEnd(position: Position): Boolean {
        if (isWall(position)) return false

        return position.neighbors()
            .filter { isWall(it) }
            .size > 2
    }

    private fun TopologyMap.removePillars(removalPercent: Double) {
        for (wallPos in getAllWallPositions()) {
            if (Math.random() < removalPercent && wallPos.neighbors().none { isWallOrDoor(it) }) {
                set(wallPos, TopologyType.Floor)
            }
        }
    }

    /**
     * Get all the wall positions on the given level, excluding the outermost border of walls.
     */
    private fun TopologyMap.getAllWallPositions(shouldShuffle: Boolean = true): List<Position> {
        val wallPositions = Position(width - 1, height - 1).toList().filter {
            !it.isOutOfBounds(this) && isWall(it)
        }

        return if (shouldShuffle) wallPositions.shuffled() else wallPositions
    }

    private fun TopologyMap.isWall(position: Position): Boolean {
        return get(position) == TopologyType.Wall
    }

    private fun TopologyMap.isWallOrDoor(position: Position): Boolean {
        return when (get(position)) {
            TopologyType.Wall, TopologyType.Door -> true
            else -> false
        }
    }

    private fun TopologyMap.area(): Int {
        return width * height
    }

    companion object {
        private const val WALL_REGION_ID = -1
    }
}