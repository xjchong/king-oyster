package com.helloworldramen.kingoyster.worldgen.topology

sealed class TopologyType {
    object Wall : TopologyType()
    object Floor : TopologyType()
    object Door : TopologyType()
}
