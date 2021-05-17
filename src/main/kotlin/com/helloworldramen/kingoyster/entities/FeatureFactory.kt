package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.attributes.AppearanceInfo
import com.helloworldramen.kingoyster.oyster.Entity

object FeatureFactory {

    fun wall() = Entity(
        attributes = listOf(
            AppearanceInfo('#')
        )
    )
}