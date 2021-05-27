package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

class PhysicalPart(
    var isPassable: Boolean = true,
    var doesBlockVision: Boolean = false
) : Part {

    override fun copy(): Part {
        return PhysicalPart(isPassable, doesBlockVision)
    }
}

fun Entity.isPassable() = (find<PhysicalPart>()?.isPassable == false).not()