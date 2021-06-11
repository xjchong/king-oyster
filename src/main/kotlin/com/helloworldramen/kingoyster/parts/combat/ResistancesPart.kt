package com.helloworldramen.kingoyster.parts.combat

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class ResistancesPart(
    val resistanceForDamageType: Map<DamageType, Double> = mapOf(),
    val resistanceForElementType: Map<ElementType, Double> = mapOf()
) : Part {

    override fun copy(): Part {
        return ResistancesPart(resistanceForDamageType.toMap(), resistanceForElementType.toMap())
    }

    fun resFactor(damageType: DamageType): Double {
        return resistanceForDamageType[damageType] ?: 1.0
    }

    fun resFactor(elementType: ElementType): Double {
        return resistanceForElementType[elementType] ?: 1.0
    }
}

fun Entity.resFactor(damageType: DamageType, elementType: ElementType): Double {
    val resistancesPart = find<ResistancesPart>() ?: return 1.0

    return resistancesPart.resFactor(damageType) * resistancesPart.resFactor(elementType)
}