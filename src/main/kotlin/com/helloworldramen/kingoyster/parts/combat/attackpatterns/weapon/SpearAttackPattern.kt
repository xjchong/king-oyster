package com.helloworldramen.kingoyster.parts.combat.attackpatterns.weapon

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.AttackPattern
import com.helloworldramen.kingoyster.parts.combat.isKillable
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.isPassable

class SpearAttackPattern(
    private val powerFactor: Double = 0.0,
    private val damageType: DamageType = DamageType.Stab,
    private val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val nextPosition = currentPosition + direction.vector

        // The square in front of the user must be passable or attackable.
        if (context.entitiesAt(nextPosition)?.all { it.isPassable() || (it.isKillable() && it.isEnemyOf(entity)) } != true) {
            return false
        }

        return getHitPositions(context, entity, direction).any { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val currentPosition = context.positionOf(entity) ?: return listOf()

        return listOf(
            currentPosition + direction.vector,
            currentPosition + (direction.vector * 2)
        )
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        val currentPosition = context.positionOf(entity) ?: return mapOf()

        return getHitPositions(context, entity, direction).associateWith {
            // Spear does more damage from a distance.
            val distanceFactor = if (currentPosition.distanceFrom(it) > 1) 2.0 else 1.0

            DamageInfo(powerFactor * distanceFactor, damageType, elementType)
        }
    }

    private fun getHitPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val hitPosition = telegraphPositions(context, entity, direction).firstOrNull { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }

        return listOfNotNull(hitPosition)
    }
}