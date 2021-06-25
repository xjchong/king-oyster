package com.helloworldramen.kingoyster.parts.combat.attackpatterns

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.statuseffects.StatusEffect
import com.helloworldramen.kingoyster.parts.isEnemyOf

open class BasicAttackPattern(
    protected val powerFactor: Double = 0.0,
    protected val damageType: DamageType = DamageType.Special,
    protected val elementType: ElementType = ElementType.None,
    protected val statusEffect: StatusEffect? = null
) : AttackPattern() {

    override fun hitPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val currentPosition = context.positionOf(entity) ?: return listOf()

        return listOf(currentPosition + direction.vector)
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        return hitPositions(context, entity, direction).associateWith {
            DamageInfo(
                powerFactor = powerFactor,
                damageType = damageType,
                elementType = elementType,
                statusEffect = statusEffect
            )
        }
    }

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        return hitPositions(context, entity, direction).any { position ->
            context.entitiesAt(position).any { it.isEnemyOf(entity) }
        }
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        return hitPositions(context, entity, direction)
    }
}