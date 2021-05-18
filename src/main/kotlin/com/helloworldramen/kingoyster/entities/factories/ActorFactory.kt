package com.helloworldramen.kingoyster.entities.factories

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.attributes.Appearance
import com.helloworldramen.kingoyster.entities.facets.Movable

object ActorFactory {

    fun player() = GameEntity(
        attributes = listOf(
            Appearance('@')
        ),
        facets = listOf(
            Movable
        ),
        requiresInput = true
    )
}