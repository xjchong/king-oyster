package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class PhysicalPart(
    var isPassable: Boolean = true,
    var doesBlockVision: Boolean = false,
    var isBarrier: Boolean = false,
    var isCorporeal: Boolean = true
) : Part {

    override fun copy(): Part {
        return PhysicalPart(isPassable, doesBlockVision, isCorporeal)
    }
}

fun Entity.isPassable(): Boolean = (find<PhysicalPart>()?.isPassable == false).not()
fun Entity.isBarrier(): Boolean = find<PhysicalPart>()?.isBarrier == true
fun Entity.isCorporeal(): Boolean = (find<PhysicalPart>()?.isCorporeal == false).not()
fun Entity.canPass(otherEntity: Entity): Boolean = isCorporeal().not() || otherEntity.isPassable()