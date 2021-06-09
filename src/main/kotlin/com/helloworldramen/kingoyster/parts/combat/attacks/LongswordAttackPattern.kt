package com.helloworldramen.kingoyster.parts.combat.attacks

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.*
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
        val hitPositions = getHitPositions(context, entity, direction)
        val landedHitCount = hitPositions.sumBy { position ->
            if (context.entitiesAt(position)?.any { it.has<CombatPart>() } == true) 1 else 0
        }

        // Longsword does more damage depending on how many hits landed.
        val landedFactor = when {
            landedHitCount >= 2 -> 2.0
            else -> 1.0
        }

        return getHitPositions(context, entity, direction).associateWith {
            DamageInfo(powerFactor * landedFactor, damageType, elementType)
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