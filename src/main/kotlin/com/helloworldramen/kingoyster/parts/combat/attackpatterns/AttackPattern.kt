package com.helloworldramen.kingoyster.parts.combat.attackpatterns

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import kotlin.math.roundToInt

abstract class AttackPattern {

    abstract fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean
    abstract fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position>
    protected abstract fun calculateDamageForPosition(context: Context, entity: Entity, direction: Direction): Map<Position, DamageInfo>
    protected open fun beforeEffect(context: Context, entity: Entity, direction: Direction) {}
    protected open fun afterEffect(context: Context, entity: Entity, direction: Direction) {}

    fun execute(context: Context, entity: Entity, direction: Direction, power: Int): Boolean {
        if (!isUsable(context, entity, direction)) return false

        beforeEffect(context, entity, direction)

        val damageForPosition = calculateDamageForPosition(context, entity, direction)

        damageForPosition.forEach { (position, damageInfo) ->
            val amount = (power * damageInfo.powerFactor).roundToInt()

            context.applyAction(position,
                Damage(
                    context = context,
                    actor = entity,
                    amount = amount,
                    damageType = damageInfo.damageType,
                    elementType = damageInfo.elementType,
                    statusEffect = damageInfo.statusEffect
                )
            )
        }

        afterEffect(context, entity, direction)

        return true
    }
}