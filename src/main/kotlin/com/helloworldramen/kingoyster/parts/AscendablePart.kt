package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Ascend
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.GameOverEvent
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Part
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.utilities.worldgen.DrunkGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.DungeonGenerationStrategy
import com.helloworldramen.kingoyster.utilities.worldgen.WorldGenerator
import kotlin.random.Random

class AscendablePart : Part {

    override fun copy(): Part {
        return AscendablePart()
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is Ascend -> respondToAscend(action)
            else -> false
        }
    }

    private fun respondToAscend(action: Ascend): Boolean {
        val (context, entity) = action

        if (++context.level > Context.MAX_WORLD_LEVEL) {
            EventBus.post(GameOverEvent(true))
        } else {
            val generationStrategy = if (Random.nextBoolean()) DrunkGenerationStrategy else DungeonGenerationStrategy

            entity.find(MemoryPart::class)?.clear()
            WorldGenerator.repopulate(context.world, generationStrategy, entity, context.world[entity])
        }

        return true
    }
}