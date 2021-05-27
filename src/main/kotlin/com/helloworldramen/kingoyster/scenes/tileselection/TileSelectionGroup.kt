package com.helloworldramen.kingoyster.scenes.tileselection

data class TileSelectionGroup(
    val groupIndex: Int,
    val northNeighborIndex: Int,
    val eastNeighborIndex: Int,
    val southNeighborIndex: Int,
    val westNeighborIndex: Int
)