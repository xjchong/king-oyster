package com.helloworldramen.kingoyster.entities.features

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.PhysicalPart

class Wall : Entity(
    PhysicalPart(
        isPassable = false
    )
)