package com.helloworldramen.kingoyster.parts.combat.attackpatterns.weapon

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.*
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.AttackPattern
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.isPassable

class GreatswordAttackPattern(
    private val powerFactor: Double = 0.0,
    private val damageType: DamageType = DamageType.Cut,
    private val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val forwardPosition = currentPosition.withRelative(direction.vector)

        // The square in front of the user must be passable or attackable.
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

        // Greatsword increases in damage the more things are hit.
        val landedFactor = when {
            landedHitCount >= 3 -> 2.0
            landedHitCount == 2 -> 1.5
            else -> 1.0
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

        return listOf(
            currentPosition.withRelative(direction.vector),
            currentPosition.withRelative(-1, 1).rotated(currentPosition, direction),
            currentPosition.withRelative(1, 1).rotated(currentPosition, direction)
        )
    }
}