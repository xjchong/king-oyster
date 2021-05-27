package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.ai.GameAiConsideration
import com.helloworldramen.kingoyster.ai.GameAiContext
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.parts.PhysicalPart
import com.helloworldramen.kingoyster.parts.faction
import com.helloworldramen.kingoyster.parts.visiblePositions
import kotlin.math.pow
import kotlin.math.sqrt

class ChaseRandomEnemyOption(vararg considerations: GameAiConsideration) : GameAiOption(considerations.toList()) {

    override fun execute(aiContext: GameAiContext): Boolean {
        val (context, chaser) = aiContext
        val world = context.world
        val currentPosition = context.world[chaser] ?: return false
        val chaserFaction = chaser.faction() ?: return false
        val visiblePositions = chaser.visiblePositions()
        val enemyPosition = visiblePositions.shuffled().firstOrNull { visiblePosition ->
            world[visiblePosition]?.any { visibleEntity ->
                visibleEntity.faction() ?: chaserFaction != chaserFaction
            } == true
        } ?: return false

        val greedyPosition = currentPosition.neighborsShuffled()
            .filter {
                world[it]?.none { entity -> entity.find(PhysicalPart::class)?.isPassable == false } == true
            }
            .minByOrNull {
                it.distanceFrom(enemyPosition)
            } ?: return false

        return chaser.respondToAction(Move(context, chaser, greedyPosition))
    }

    private fun Position.distanceFrom(position: Position): Double {
        return sqrt((x - position.x).toDouble().pow(2) + (y - position.y).toDouble().pow(2))
    }
}