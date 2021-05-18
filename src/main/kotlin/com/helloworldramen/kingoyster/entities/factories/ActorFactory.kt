package com.helloworldramen.kingoyster.entities.factories

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.attributes.AppearanceInfo
import com.helloworldramen.kingoyster.entities.facets.Movable
import com.helloworldramen.kingoyster.oyster.Entity

object ActorFactory {

    fun player() = GameEntity(
        attributes = listOf(
            AppearanceInfo('@')
        ),
        facets = listOf(
            Movable
        ),
        requiresInput = true
    )
}