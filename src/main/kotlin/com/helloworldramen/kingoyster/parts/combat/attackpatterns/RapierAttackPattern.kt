package com.helloworldramen.kingoyster.parts.combat.attackpatterns

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.*
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.isPassable

// Necrodancer inspired weapon.
class RapierAttackPattern(
    private val powerFactor: Double,
    private val damageType: DamageType = DamageType.Stab,
    private val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val nextPosition = currentPosition.withRelative(direction.vector)

        // The square in front of the user must be passable or attackable.
        if (context.entitiesAt(nextPosition)?.all { it.isPassable() || (it.has<CombatPart>() && it.isEnemyOf(entity)) } != true) {
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
        val currentPosition = context.positionOf(entity) ?: return mapOf()

        return getHitPositions(context, entity, direction).associateWith {
            // Rapier does more damage when lunging.
            val distanceFactor = if (currentPosition.distanceFrom(it) > 1) 2.0 else 0.8

            DamageInfo(powerFactor * distanceFactor, damageType, elementType)
        }
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val currentPosition = context.positionOf(entity) ?: return listOf()

        return listOf(
            currentPosition.withRelative(direction.vector),
            currentPosition.withRelative(direction.vector * 2)
        )
    }

    override fun afterEffect(context: Context, entity: Entity, direction: Direction) {
        val followupPath = getFollowupPath(context, entity, direction)

        for (followPosition in followupPath) {
            if (!entity.respondToAction(Move(context, entity, followPosition, timeFactor = 0.0))) break
        }
    }

    private fun getFollowupPath(context: Context, entity: Entity, direction: Direction): List<Position> {
        // Move to the square next to the user.
        val currentPosition = context.positionOf(entity) ?: return listOf()
        val nextPosition = currentPosition + direction.vector

        return if (context.entitiesAt(nextPosition)?.all { it.isPassable() } == true) {
            listOf(nextPosition)
        } else {
            listOf()
        }
    }

    private fun getHitPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val hitPosition = telegraphPositions(context, entity, direction).firstOrNull { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }

        return listOfNotNull(hitPosition)
    }
}