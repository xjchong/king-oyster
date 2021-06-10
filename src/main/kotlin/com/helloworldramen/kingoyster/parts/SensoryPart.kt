package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.utilities.ShadowCasting

class SensoryPart(
    var visionRange: Int,
    var canHavePlayerSense: Boolean = false,
    var hasPlayerSense: Boolean = false
) : Part {
    var visiblePositions: Set<Position> = setOf()
        private set
    var isOmniscient: Boolean = false // FOR DEBUG ONLY! :)

    private constructor(visionRange: Int, canHavePlayerSense: Boolean, hasPlayerSense: Boolean,
                        visiblePositions: Set<Position>): this(visionRange, canHavePlayerSense, hasPlayerSense) {
        this.visiblePositions = visiblePositions.toSet()
    }

    override fun copy(): Part {
        return SensoryPart(visionRange, canHavePlayerSense, hasPlayerSense, visiblePositions)
    }

    override fun update(context: Context, partOwner: Entity) {
        if (isOmniscient) {
            updateAsOmniscient(context)
            return
        }

        if (canHavePlayerSense) {
            handlePlayerSense(context, partOwner)
        }

        val world = context.world
        val currentPosition = world[partOwner] ?: return
        val nextVisiblePositions: MutableList<Position> = mutableListOf()
        val memoryPart = partOwner.find(MemoryPart::class)

        ShadowCasting.computeFOV(currentPosition.x, currentPosition.y, visionRange,
            isBlocking = { x, y ->
                world[x, y]?.any { it.find(PhysicalPart::class)?.doesBlockVision == true } ?: false
            }, markVisible = { x, y ->
                val visiblePosition = Position(x, y)
                nextVisiblePositions.add(visiblePosition)
                memoryPart?.remember(context, visiblePosition)
            })

        if (hasPlayerSense) {
            context.positionOf(context.player)?.let { nextVisiblePositions.add(it) }
        }

        visiblePositions = nextVisiblePositions.toSet()
    }

    /**
     * Player sense allows an entity to see where the player is without having actual line of sight
     * (i.e., with shadow casting). This is useful for allowing an entity to chase a player that is just a little out
     * of sight. However, the entity should only be able to do this after sensing the player, i.e., having seen
     * the player.
     */
    private fun handlePlayerSense(context: Context, partOwner: Entity) {
        if (hasPlayerSense) return

        val playerPosition = context.positionOf(context.player) ?: return
        val currentPosition = context.positionOf(partOwner) ?: return

        if (playerPosition.distanceFrom(currentPosition) > visionRange) return

        if (visiblePositions.contains(playerPosition)) {
            hasPlayerSense = true
        }
    }

    private fun updateAsOmniscient(context: Context) {
        val world = context.world

        if (visiblePositions.size >= world.width * world.height) return

        visiblePositions = Position(world.width - 1, world.height - 1).map { it }.toSet()
    }
}

fun Entity.visiblePositions(): Set<Position> = find<SensoryPart>()?.visiblePositions ?: setOf()
