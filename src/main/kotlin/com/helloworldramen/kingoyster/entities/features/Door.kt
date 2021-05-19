package com.helloworldramen.kingoyster.entities.features

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.PhysicalPart
import com.helloworldramen.kingoyster.parts.PortalPart

class Door(isOpen: Boolean = false) : Entity(
    PhysicalPart(
        isPassable = isOpen,
        doesBlockVision = !isOpen
    ),
    PortalPart(isOpen)
)