package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Part

class WeaponPart(
    val attackInfo: AttackInfo
) : Part {

    override fun copy(): Part {
        return WeaponPart(attackInfo)
    }
}