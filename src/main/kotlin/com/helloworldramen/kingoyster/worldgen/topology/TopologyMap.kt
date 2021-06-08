package com.helloworldramen.kingoyster.worldgen.topology

import com.helloworldramen.kingoyster.architecture.Position

class TopologyMap(val width: Int, val height: Int) {

    private val typeForPosition: MutableMap<Position, TopologyType> =
        Position(width - 1, height - 1).map { it }.associateWith { TopologyType.Floor }.toMutableMap()

    fun clear() {
        typeForPosition.clear()
    }

    operator fun get(position: Position): TopologyType? {
        return typeForPosition[position]
    }

    operator fun set(position: Position, type: TopologyType) {
        typeForPosition[position] = type
    }
}