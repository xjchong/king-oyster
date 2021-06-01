package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.DamageInfo
import com.helloworldramen.kingoyster.parts.DamageType
import com.helloworldramen.kingoyster.parts.ElementType

data class Damage(
    override val context: Context,
    override val actor: Entity,
    val amount: Int,
    val damageType: DamageType,
    val elementType: ElementType = ElementType.None
) : Action {

    override val timeFactor: Double = 0.0
}