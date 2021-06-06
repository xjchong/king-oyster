package com.helloworldramen.kingoyster.parts.combat.attacks

import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType

class DaggerAttackPattern(
    powerFactor: Double,
    damageType: DamageType = DamageType.Cut,
    elementType: ElementType = ElementType.None
) : BasicAttackPattern(powerFactor, damageType, elementType)