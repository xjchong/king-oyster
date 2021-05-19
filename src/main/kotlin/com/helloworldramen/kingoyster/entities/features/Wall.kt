package com.helloworldramen.kingoyster.entities.features

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.parts.Physical

class Wall : Entity(
    Physical(
        isPassable = false
    )
)