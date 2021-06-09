package com.helloworldramen.kingoyster.parts.combat.attacks

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.AttackPattern
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.isEnemyOf

open class BasicAttackPattern(
    protected val powerFactor: Double = 0.0,
    protected val damageType: DamageType = DamageType.Special,
    protected val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val hitPosition = getHitPosition(context, entity, direction) ?: return false

        return (context.entitiesAt(hitPosition)?.any { it.isEnemyOf(entity) } == true)
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        val hitPosition = getHitPosition(context, entity, direction) ?: return mapOf()

        return mapOf(hitPosition to DamageInfo(powerFactor, damageType, elementType))
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        return listOfNotNull(getHitPosition(context, entity, direction))
    }

    protected fun getHitPosition(context: Context, entity: Entity, direction: Direction): Position? {
        val currentPosition = context.positionOf(entity) ?: return null

        return currentPosition + direction.vector
    }
}