package com.helloworldramen.kingoyster.parts.combat.attackpatterns.movement

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.AttackPattern
import com.helloworldramen.kingoyster.parts.combat.power
import com.helloworldramen.kingoyster.parts.isPassable

class ShoulderBashPattern(
    private val powerFactor: Double,
    private val damageType: DamageType = DamageType.Bash,
    private val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val adjacentPosition = currentPosition + direction.vector
        val adjacentEntities = context.entitiesAt(adjacentPosition) ?: return false

        return adjacentEntities.all { it.isPassable() }
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        return listOf() // TODO: Implement if enemies will be using this pattern.
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        val endPosition = calculateEndPosition(context, entity, direction) ?: return mapOf()

        val hitPositions = endPosition.neighbors().filter {
            it != endPosition - direction.vector
        }

        return hitPositions.associateWith { DamageInfo(powerFactor, damageType, elementType) }
    }

    override fun beforeEffect(context: Context, entity: Entity, direction: Direction) {
        val endPosition = calculateEndPosition(context, entity, direction) ?: return

        entity.respondToAction(Move(context, entity, endPosition, timeFactor = 0.0))
    }

    override fun afterEffect(context: Context, entity: Entity, direction: Direction) {
        entity.respondToAction(Damage(context, Entity.UNKNOWN, 1, DamageType.Bash))
    }

    private fun calculateEndPosition(context: Context, entity: Entity, direction: Direction): Position? {
        val currentPosition = context.positionOf(entity) ?: return null

        return context.straightPathWhile(currentPosition, direction) { position ->
            val entities = context.entitiesAt(position)

            entities != null && (entities.all { it.isPassable() } || entities.contains(entity))
        }.lastOrNull()
    }
}