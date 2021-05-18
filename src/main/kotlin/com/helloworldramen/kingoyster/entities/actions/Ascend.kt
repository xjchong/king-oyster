package com.helloworldramen.kingoyster.entities.actions

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.game.GameContext
import com.helloworldramen.kingoyster.oyster.Action

data class Ascend(
    val context: GameContext,
    val user: GameEntity
) : Action
