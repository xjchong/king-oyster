package com.helloworldramen.kingoyster.entities.factories

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.attributes.AppearanceInfo
import com.helloworldramen.kingoyster.oyster.Entity

object FeatureFactory {

    fun wall() = GameEntity(
        attributes = listOf(
            AppearanceInfo('#')
        )
    )
}