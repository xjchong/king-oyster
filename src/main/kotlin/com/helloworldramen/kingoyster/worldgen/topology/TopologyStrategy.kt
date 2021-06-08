package com.helloworldramen.kingoyster.worldgen.topology

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.entities.FeatureFactory
import com.helloworldramen.kingoyster.worldgen.metadata.WorldFlavor

abstract class TopologyStrategy {

    protected abstract fun generateTopologyMap(
        width: Int,
        height: Int,
        playerPosition: Position?
    ): TopologyMap

    fun terraform(
        flavor: WorldFlavor,
        width: Int,
        height: Int,
        playerPosition: Position?
    ): Pair<World, WorldFlavor> {
        val topologyMap = generateTopologyMap(width, height, playerPosition)
        val world = World(width, height)

        Position(width - 1, height - 1).forEach { position ->
            when (topologyMap[position]) {
                TopologyType.Wall -> world.add(flavor.wallFlavour.wallFactoryFn(), position)
                TopologyType.Door -> world.add(topologyMap.orientatedDoor(position), position)
                else -> world.removeAll(position)
            }
        }

        return Pair(world, flavor)
    }

    protected fun TopologyMap.fill(topologyType: TopologyType) {
        clear()
        Position(width - 1, height - 1).forEach {
            set(it, topologyType)
        }
    }

    private fun TopologyMap.orientatedDoor(position: Position): Entity {
        val horizontalCount = listOfNotNull(get(position.west()), get(position.east()))
            .filterNot { it == TopologyType.Floor }
            .size
        val verticalCount = listOfNotNull(get(position.north()), get(position.south()))
            .filterNot { it == TopologyType.Floor }
            .size

        return FeatureFactory.door(horizontalCount >= verticalCount)()
    }

    protected open fun Position.isOutOfBounds(topologyMap: TopologyMap): Boolean {
        return x <= 0 || y <= 0 || x >= topologyMap.width - 1 || y >= topologyMap.height - 1
    }
}