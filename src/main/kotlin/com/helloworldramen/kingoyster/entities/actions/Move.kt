package com.helloworldramen.kingoyster.entities.actions

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.models.Position
import com.helloworldramen.kingoyster.oyster.Action

data class Move(
    val entity: GameEntity,
    val position: Position
) : Action
