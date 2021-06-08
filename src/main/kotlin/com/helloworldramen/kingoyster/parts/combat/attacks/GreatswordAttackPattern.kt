package com.helloworldramen.kingoyster.parts.combat.attacks

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.*
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

        return getHitPositions(currentPosition, direction).any { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        val currentPosition = context.positionOf(entity) ?: return mapOf()

        return getHitPositions(currentPosition, direction).associateWith {
            DamageInfo(powerFactor, damageType, elementType)
        }
    }

    private fun getHitPositions(origin: Position, direction: Direction): List<Position> {
        return listOf(
            origin.withRelative(direction.vector),
            origin.withRelative(-1, 1).rotated(origin, direction),
            origin.withRelative(1, 1).rotated(origin, direction)
        )
    }
}