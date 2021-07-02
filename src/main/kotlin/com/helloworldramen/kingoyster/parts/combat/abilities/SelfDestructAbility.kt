package com.helloworldramen.kingoyster.parts.combat.abilities

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.statuseffects.StatusEffect

class SelfDestructAbility(
    val amount: Int,
    val damageType: DamageType = DamageType.Special,
    val elementType: ElementType = ElementType.None,
    val statusEffect: StatusEffect? = null
) : Ability() {

    override fun isUsable(context: Context, entity: Entity, user: Entity): Boolean {
        return true
    }

    override fun effect(context: Context, entity: Entity, user: Entity) {
        val currentPosition = context.positionOf(entity) ?: return
        val hitPositions = listOf(currentPosition) + currentPosition.neighbors() + currentPosition.diagonalNeighbors()
        val damageAction = Damage(
            context = context,
            actor = entity,
            amount = amount,
            damageType = damageType,
            elementType = elementType,
            statusEffect = statusEffect
        )

        entity.respondToAction(Damage(context, entity, 9999, DamageType.Special))
        context.applyAction(hitPositions, damageAction)
    }
}