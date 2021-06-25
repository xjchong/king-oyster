package com.helloworldramen.kingoyster.worldgen.topology

import com.helloworldramen.kingoyster.architecture.Position

class DrunkTopology(private val clearPercentage: Double = DEFAULT_CLEAR_PERCENTAGE) : TopologyStrategy() {

    override fun generateTopologyMap(width: Int, height: Int, playerPosition: Position?): TopologyMap {
        return TopologyMap(width, height).apply {
            fill(TopologyType.Wall)
            drunkWalk(this, clearPercentage, Position(width / 2, height / 2))
        }
    }

    private fun drunkWalk(topologyMap: TopologyMap, clearPercentage: Double, startingPosition: Position) {
        var currentPosition = startingPosition
        var clearCount = 1
        var currentWalkLength = 0
        val maxWalkLength = topologyMap.width * topologyMap.height
        val worldArea = topologyMap.width * topologyMap.height
        val vectors = listOf(
            Position(0, 1), Position(0, -1),
            Position(1, 0), Position(-1, 0)
        )

        topologyMap[currentPosition] = TopologyType.Floor

        while (clearCount / worldArea.toDouble() < clearPercentage) {
            val nextPosition = currentPosition.withRelative(vectors.random())
            if (nextPosition.isOutOfBounds(topologyMap)) continue
            currentPosition = nextPosition
            currentWalkLength++

            val currentType = topologyMap[currentPosition] ?: continue
            if (currentType == TopologyType.Floor) continue

            topologyMap[currentPosition] = TopologyType.Floor
            clearCount++

            if (currentWalkLength >= maxWalkLength) {
                currentPosition = startingPosition
                currentWalkLength = 0
            }
        }
    }

    companion object {
        private const val DEFAULT_CLEAR_PERCENTAGE = 0.45
    }
}