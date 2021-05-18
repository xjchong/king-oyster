package com.helloworldramen.kingoyster.entities.factories

import com.helloworldramen.kingoyster.entities.GameEntity
import com.helloworldramen.kingoyster.entities.attributes.Appearance
import com.helloworldramen.kingoyster.entities.attributes.Impassable
import com.helloworldramen.kingoyster.entities.facets.Ascendable

object FeatureFactory {

    fun wall() = GameEntity(
        attributes = listOf(
            Appearance('#'),
            Impassable
        )
    )

    fun stairsUp() = GameEntity(
        attributes = listOf(
            Appearance('<')
        ),
        facets = listOf(
            Ascendable
        )
    )
}