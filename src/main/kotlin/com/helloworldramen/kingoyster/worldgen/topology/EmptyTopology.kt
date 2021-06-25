package com.helloworldramen.kingoyster.worldgen.topology

import com.helloworldramen.kingoyster.architecture.Position

object EmptyTopology : TopologyStrategy() {

    override fun generateTopologyMap(width: Int, height: Int, playerPosition: Position?): TopologyMap {
        return TopologyMap(width, height)
    }
}