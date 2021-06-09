package com.helloworldramen.kingoyster.parts.combat.attackpatterns

import com.helloworldramen.kingoyster.actions.Move
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
        val willStepForward = context.entitiesAt(forwardPosition)?.all { it.isPassable() } == true
        val attackPosition = if (willStepForward) forwardPosition else currentPosition

        if (context.entitiesAt(attackPosition + direction.vector)?.all { it.isPassable() || it.has<CombatPart>() } != true) {
            return false
        }

        return getHitPositions(attackPosition, direction).any { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }
    }

    override fun beforeEffect(context: Context, entity: Entity, direction: Direction) {
        val currentPosition = context.positionOf(entity) ?: return

        entity.respondToAction(Move(context, entity, currentPosition + direction.vector, timeFactor = 0.0))
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        val currentPosition = context.positionOf(entity) ?: return mapOf()
        val hitPositions = getHitPositions(currentPosition, direction)
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
        val currentPosition = context.positionOf(entity) ?: return listOf()

        return getHitPositions(currentPosition, direction)
    }

    private fun getHitPositions(attackPosition: Position, direction: Direction): List<Position> {
        val behindPosition = attackPosition - direction.vector
        val allNeighbors = attackPosition.neighbors() + attackPosition.diagonalNeighbors()

        return allNeighbors.filter { it != behindPosition }
    }
}