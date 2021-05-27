package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Part

class CombatPart(
    val attackPotency: Int
) : Part {

    override fun copy(): Part {
        return CombatPart(attackPotency)
    }
}