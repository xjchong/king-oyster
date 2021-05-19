package com.helloworldramen.kingoyster.entities.features

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.Physical
import com.helloworldramen.kingoyster.parts.Portal

class Door(isOpen: Boolean = false) : Entity(
    Physical(
        isPassable = isOpen
    ),
    Portal(isOpen)
)