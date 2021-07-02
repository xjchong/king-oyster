package com.helloworldramen.kingoyster.parts.combat.abilities

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.statuseffects.StatusEffect
import com.helloworldramen.kingoyster.parts.isBarrier
import com.helloworldramen.kingoyster.utilities.FloodFill

class SelfDestructAbility(
    val amount: Int,
    val damageType: DamageType = DamageType.Special,
    val elementType: ElementType = ElementType.None,
    val statusEffect: StatusEffect? = null
) : Ability() {

    override fun telegraphPositions(context: Context, entity: Entity, user: Entity): List<Position> {
        return hitPositions(context, entity)
    }

    override fun isUsable(context: Context, entity: Entity, user: Entity): Boolean {
        return true
    }

    override fun effect(context: Context, entity: Entity, user: Entity) {
        val hitPositions = hitPositions(context, entity)
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

    private fun hitPositions(context: Context, entity: Entity): List<Position> {
        val currentPosition = context.positionOf(entity) ?: return listOf()

        return FloodFill.fill(currentPosition.x, currentPosition.y, 3) { x, y ->
            val position = Position(x, y)

            context.entitiesAt(position).any { it.isBarrier() && !it.has<CombatPart>() }
        }.map { (x, y) ->
            Position(x, y)
        }
    }
}