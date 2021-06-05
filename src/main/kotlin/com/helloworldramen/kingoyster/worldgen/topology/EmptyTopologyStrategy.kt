package com.helloworldramen.kingoyster.worldgen.topology

import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World

object EmptyTopologyStrategy : TopologyStrategy() {

    override fun generate(width: Int, height: Int, playerPosition: Position?): World {
        return World(width, height)
    }
}