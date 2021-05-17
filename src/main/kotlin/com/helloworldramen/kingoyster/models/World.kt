package com.helloworldramen.kingoyster.models

import com.helloworldramen.kingoyster.oyster.Entity

class World(val width: Int, val height: Int) {

    private val entitiesForPosition: MutableMap<Position, MutableList<Entity>> =
        mutableMapOf<Position, MutableList<Entity>>().apply {
            Position(width - 1, height - 1).forEach {
                this[it] = mutableListOf()
            }
        }

    operator fun get(position: Position): MutableList<Entity>? = entitiesForPosition[position]

    operator fun get(x: Int, y: Int): MutableList<Entity>? = entitiesForPosition[Position(x, y)]
}