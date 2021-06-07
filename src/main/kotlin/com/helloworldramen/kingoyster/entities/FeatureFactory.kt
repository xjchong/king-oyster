package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.AscendablePart
import com.helloworldramen.kingoyster.parts.PhysicalPart
import com.helloworldramen.kingoyster.parts.DoorPart

object FeatureFactory {

    fun door(isOpen: Boolean): EntityFactoryFn = {
        Entity(
            name = "door",
            parts = listOf(
                PhysicalPart(
                    isPassable = isOpen,
                    doesBlockVision = !isOpen
                ),
                DoorPart(isOpen)
            )
        )
    }

    fun stairs(): EntityFactoryFn = {
        Entity(
            name = "stairs",
            parts = listOf(
                AscendablePart()
            )
        )
    }

    fun wall(): EntityFactoryFn = {
        Entity(
            name = "wall",
            parts = listOf(
                PhysicalPart(
                    isPassable = false,
                    doesBlockVision = true
                )
            )
        )
    }
}