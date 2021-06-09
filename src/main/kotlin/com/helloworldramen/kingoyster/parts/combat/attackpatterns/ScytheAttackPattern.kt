package com.helloworldramen.kingoyster.parts.combat.attackpatterns

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.*
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.isPassable

class ScytheAttackPattern(
    private val powerFactor: Double = 0.0,
    private val damageType: DamageType = DamageType.Cut,
    private val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val forwardPosition = currentPosition.withRelative(direction.vector)

        if (context.entitiesAt(forwardPosition)?.all { it.isPassable() || it.has<CombatPart>() } != true) {
            return false
        }

        return getHitPositions(context, entity, direction).any { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }
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

        val landedFactor = when {
            landedHitCount >= 7 -> 6.2
            landedHitCount == 6 -> 4.8
            landedHitCount == 5 -> 3.6
            landedHitCount == 4 -> 2.6
            landedHitCount == 3 -> 1.8
            landedHitCount == 2 -> 1.2
            else -> 0.8
        }

        return hitPositions.associateWith {
            DamageInfo(powerFactor * landedFactor, damageType, elementType)
        }
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        return getHitPositions(context, entity, direction)
    }

    private fun getHitPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val currentPosition = context.positionOf(entity) ?: return listOf()
        val behindPosition = currentPosition - direction.vector
        val allNeighbors = currentPosition.neighbors() + currentPosition.diagonalNeighbors()

        return allNeighbors.filter { it != behindPosition }
    }
}