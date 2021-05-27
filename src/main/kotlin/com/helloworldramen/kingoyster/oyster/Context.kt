package com.helloworldramen.kingoyster.oyster


class Context(val world: World) {
    var player = Entity.UNKNOWN
    var level = 1

    companion object {
        const val MAX_WORLD_LEVEL = 10

        val UNKNOWN: Context = Context(World(0, 0))
    }
}