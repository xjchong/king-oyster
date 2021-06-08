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

class LongswordAttackPattern(
    private val powerFactor: Double = 0.0,
    private val damageType: DamageType = DamageType.Cut,
    private val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val nextPosition = currentPosition.withRelative(direction.vector)

        return (context.entitiesAt(nextPosition)?.any { it.isEnemyOf(entity) } == true)
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        return getHitPositions(context, entity, direction).associateWith {
            DamageInfo(powerFactor, damageType, elementType)
        }
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        return listOfNotNull(context.positionOf(entity)?.withRelative(direction.vector))
    }

    private fun getHitPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val currentPosition = context.positionOf(entity) ?: return listOf()

        return listOf(
            currentPosition + direction.vector,
            currentPosition + (direction.vector * 2)
        )
    }
}