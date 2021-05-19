package com.helloworldramen.kingoyster.oyster


class Context(val world: World) {
    var level = 1

    companion object {
        const val MAX_WORLD_LEVEL = 10
    }
}