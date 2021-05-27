package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.AscendablePart
import com.helloworldramen.kingoyster.parts.PhysicalPart
import com.helloworldramen.kingoyster.parts.DoorPart

object FeatureFactory {

    fun door(isOpen: Boolean) = Entity(
        name = "door",
        parts = listOf(
            PhysicalPart(
                isPassable = isOpen,
                doesBlockVision = !isOpen
            ),
            DoorPart(isOpen)
        )
    )

    fun stairs() = Entity(
        name = "stairs",
        parts = listOf(
            AscendablePart()
        )
    )

    fun wall() = Entity(
        name = "wall",
        parts = listOf(
            PhysicalPart(
                isPassable = false,
                doesBlockVision = true
            )
        )
    )
}