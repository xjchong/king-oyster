package com.helloworldramen.kingoyster.entities.factories

import com.helloworldramen.kingoyster.entities.attributes.AppearanceInfo
import com.helloworldramen.kingoyster.oyster.Entity

object ActorFactory {

    fun player() = Entity(
        attributes = listOf(
            AppearanceInfo('@')
        )
    )
}