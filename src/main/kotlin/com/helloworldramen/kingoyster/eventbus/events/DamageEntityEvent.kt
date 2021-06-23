package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType

data class DamageEntityEvent(
    val source: Entity,
    val target: Entity,
    val amount: Int,
    val damageType: DamageType,
    val elementType: ElementType
) : Event