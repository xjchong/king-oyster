package com.helloworldramen.kingoyster.entities.features

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.PhysicalPart

class Wall : Entity(
    name = "wall",
    parts = listOf(
        PhysicalPart(
            isPassable = false,
            doesBlockVision = true
        )
    )
)