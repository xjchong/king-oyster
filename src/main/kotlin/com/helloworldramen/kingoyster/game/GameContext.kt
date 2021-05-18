package com.helloworldramen.kingoyster.game

import com.helloworldramen.kingoyster.oyster.Context

class GameContext(val world: GameWorld): Context {

    var worldLevel = 0

    companion object {
        const val MAX_WORLD_LEVEL = 1
    }
}