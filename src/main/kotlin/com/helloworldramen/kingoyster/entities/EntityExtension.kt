package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity

val Entity.isPlayer: Boolean
    get() = name == "player"