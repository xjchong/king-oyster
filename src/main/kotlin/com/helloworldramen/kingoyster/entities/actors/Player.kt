package com.helloworldramen.kingoyster.entities.actors

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.Movement
import com.helloworldramen.kingoyster.parts.Physical

class Player : Entity(
    parts = listOf(
        Physical(
            isPassable = false
        ),
        Movement()
    ),
    requiresUpdate = true,
    requiresInput = true
)