package com.helloworldramen.kingoyster.utilities.worldgen

import com.helloworldramen.kingoyster.entities.Door
import com.helloworldramen.kingoyster.entities.Wall
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.oyster.World
import java.util.*
import kotlin.math.roundToInt

object DungeonGenerationStrategy: WorldGenerationStrategy {

    private const val WALL_REGION_ID = -1
    private const val DOOR_OPEN_CHANCE = 0.2

    private var regionIds: MutableMap<Position, Int> = mutableMapOf()
    private var nextRegionId: Int = 0
    private var mergedRegionIds: MutableSet<MutableSet<Int>> = mutableSetOf()
    override fun generate(world: World) {
        with(world) {
            clear()
            clear()
            fill(WALL_REGION_ID) { Wall() }
            placeRooms(100, 7, 4, 2.0)
            placeCorridors(0.05)
            placeDoors(0.05, 12)
            removePillars(0.5)
            removeDeadEnds()
        }
    }

    private fun getOdd(number: Int): Int {
        return if (number % 2 == 0) number + 1 else number
    }

    private fun nextGaussian(mu: Int, standardDeviation: Double): Double {
        return (Random().nextGaussian() * standardDeviation) + mu
    }

    private fun World.fill(regionId: Int, fn: () -> Entity) {
        Position(width - 1, height - 1).forEach {
            add(fn(), it)
            regionIds[it] = regionId
        }
    }

    private fun World.placeRooms(attempts: Int, meanWidth: Int, meanHeight: Int, standardDeviation: Double) {
        repeat(attempts) {
            val x = getOdd((Math.random() * width).roundToInt())
            val y = getOdd((Math.random() * height).roundToInt())
            val roomWidth = getOdd(nextGaussian(meanWidth, standardDeviation).toInt())
            val roomHeight = getOdd(nextGaussian(meanHeight, standardDeviation).toInt())

            if (isRoomSafe(x, y, roomWidth, roomHeight)) {
                placeRoom(x, y, roomWidth, roomHeight)
                nextRegionId++
            }
        }
    }

    private fun World.placeRoom(x: Int, y: Int, roomWidth: Int, roomHeight: Int) {
        val origin = Position(x, y)

        origin.forRange(origin.withRelative(roomWidth - 1, roomHeight - 1)) {
            removeAll(it)
            regionIds[it] = nextRegionId
        }
    }

    private fun World.isRoomSafe(x: Int, y: Int, roomWidth: Int, roomHeight: Int): Boolean {
        val origin = Position(x, y)
        if (isOutOfBounds(origin)) return false
        if (roomWidth < 2 || roomHeight < 2) return false // Room is too small.
        if ((x + roomWidth >= width) || (y + roomHeight >= height)) return false

        for (pos in origin.range(Position(x + roomWidth - 1, y + roomHeight- 1))) {
            if (!isWall(pos)) return false
        }

        return true
    }

    private fun World.placeCorridors(cyclePercent: Double) {
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

    private fun World.placeCorridorFrom(startPos: Position, cyclePercent: Double) {
        val directions = mutableListOf('e', 's', 'w', 'n').shuffled()
        removeAll(startPos)
        regionIds[startPos] = nextRegionId

        for (direction in directions) {
            val endPos = when (direction) {
                'e' -> startPos.withRelativeX(2)
                's' -> startPos.withRelativeY(2)
                'w' -> startPos.withRelativeX(-2)
                else -> startPos.withRelativeY(-2)
            }

            val couldCreateCycle = regionIds[endPos] == nextRegionId && Math.random() < cyclePercent

            if (isWall(endPos) && !isOutOfBounds(endPos) || couldCreateCycle) {
                startPos.forRange(endPos) { pos ->
                    removeAll(pos)
                    regionIds[pos] = nextRegionId
                }

                placeCorridorFrom(endPos, cyclePercent)
            }
        }
    }

    private fun World.placeDoors(extraDoorPercent: Double, maxExtraDoors: Int) {
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
            val isDoorOpen = Math.random() < DOOR_OPEN_CHANCE

            for (mergedSet in mergedRegionIds) {
                val intersectionSize = mergedSet.intersect(adjacentRegions).size

                if (intersectionSize == 2) needsMerge = false
                if (intersectionSize == 1) {
                    isDisjoint = false
                    mergedSet.addAll(adjacentRegions)
                }
            }

            if (needsMerge) {
                removeAll(pos)
                add(Door(isDoorOpen), pos)
                if (isDisjoint) mergedRegionIds.add(adjacentRegions)
            } else if (canCreateExtra) {
                removeAll(pos)
                add(Door(isDoorOpen), pos)
                extraDoorsCount++
            }
        }
    }

    private fun World.removeDeadEnds() {
        Position(width - 1, height - 1).forEach {
            removeDeadEnd(it)
        }
    }

    private fun World.removeDeadEnd(position: Position) {
        if (!isDeadEnd(position)) return

        add(Wall(), position)
        regionIds[position] = WALL_REGION_ID

        position.neighbors().forEach {
            removeDeadEnd(it)
        }
    }

    private fun World.isDeadEnd(position: Position): Boolean {
        if (isWall(position)) return false

        return position.neighbors()
            .filter { isWall(it) }
            .size > 2
    }

    private fun World.removePillars(removalPercent: Double) {
        for (wallPos in getAllWallPositions()) {
            if (Math.random() < removalPercent && wallPos.neighbors().none { isWallOrDoor(it) }) {
                removeAll(wallPos)
            }
        }
    }

    /**
     * Get all the wall positions on the given level, excluding the outermost border of walls.
     */
    private fun World.getAllWallPositions(shouldShuffle: Boolean = true): List<Position> {
        val wallPositions = Position(width - 1, height - 1).toList().filter {
            !isOutOfBounds(it) && isWall(it)
        }

        return if (shouldShuffle) wallPositions.shuffled() else wallPositions
    }

    private fun World.isWall(position: Position): Boolean {
        return get(position)?.any { it is Wall } ?: false
    }

    private fun World.isWallOrDoor(position: Position): Boolean {
        return get(position)?.any { it is Wall || it is Door } ?: false
    }

    private fun World.isOutOfBounds(position: Position): Boolean {
        return position.x <= 0 || position.y <= 0 || position.x >= width - 1 || position.y >= height - 1
    }
}