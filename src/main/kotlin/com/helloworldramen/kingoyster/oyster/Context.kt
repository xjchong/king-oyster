package com.helloworldramen.kingoyster.oyster


class Context(val world: World) {
    var player: Entity? = null
    var level = 1

    companion object {
        const val MAX_WORLD_LEVEL = 10

        fun UNKNOWN(): Context {
            return Context(World(0, 0))
        }
    }
}