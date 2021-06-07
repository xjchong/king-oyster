package com.helloworldramen.kingoyster.parts.combat

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class ResistancesPart(
    val cutResFactor: Double = 1.0,
    val bashResFactor: Double = 1.0,
    val stabResFactor: Double = 1.0,
    val magicResFactor: Double = 1.0,
    val fireResFactor: Double = 1.0,
    val iceResFactor: Double = 1.0,
    val voltResFactor: Double = 1.0
) : Part {

    override fun copy(): Part {
        return ResistancesPart(cutResFactor, bashResFactor, stabResFactor, magicResFactor, fireResFactor, iceResFactor, voltResFactor)
    }

    fun resFactor(damageType: DamageType): Double {
        return when (damageType) {
            DamageType.Cut -> cutResFactor
            DamageType.Bash -> bashResFactor
            DamageType.Stab -> stabResFactor
            DamageType.Magic -> magicResFactor
            else -> 1.0
        }
    }

    fun resFactor(elementType: ElementType): Double {
        return when (elementType) {
            ElementType.Fire -> fireResFactor
            ElementType.Ice -> iceResFactor
            ElementType.Volt -> voltResFactor
            else -> 1.0
        }
    }
}

fun Entity.resFactor(damageType: DamageType, elementType: ElementType): Double {
    val resistancesPart = find<ResistancesPart>() ?: return 1.0

    return resistancesPart.resFactor(damageType) * resistancesPart.resFactor(elementType)
}