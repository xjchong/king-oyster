package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.statuseffects.StatusEffect

data class Damage(
    override val context: Context,
    override val actor: Entity,
    val amount: Int,
    val damageType: DamageType,
    val elementType: ElementType = ElementType.None,
    val statusEffect: StatusEffect? = null
) : Action {

    override val world: World = context.world
    override val timeFactor: Double = 0.0
}