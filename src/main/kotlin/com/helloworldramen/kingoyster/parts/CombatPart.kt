package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Part

class CombatPart(
    val attackPotency: Int
) : Part {

    override fun copy(): Part {
        return CombatPart(attackPotency)
    }
}