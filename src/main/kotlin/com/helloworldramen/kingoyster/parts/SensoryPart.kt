package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.utilities.ShadowCasting

class SensoryPart(
    var visionRange: Int,
    var canHavePlayerSense: Boolean = false,
    var playerPosition: Position? = null
) : Part {
    var visiblePositions: Set<Position> = setOf()
        private set
    var isOmniscient: Boolean = false // FOR DEBUG ONLY! :)

    private constructor(visionRange: Int, canHavePlayerSense: Boolean, playerPosition: Position?,
                        visiblePositions: Set<Position>): this(visionRange, canHavePlayerSense, playerPosition) {
        this.visiblePositions = visiblePositions.toSet()
    }

    override fun copy(): Part {
        return SensoryPart(visionRange, canHavePlayerSense, playerPosition, visiblePositions)
    }

    override fun update(context: Context, partOwner: Entity) {
        if (isOmniscient) {
            updateAsOmniscient(context)
            return
        }

        val world = context.world
        val currentPosition = world[partOwner] ?: return
        val nextVisiblePositions: MutableList<Position> = mutableListOf()
        val memoryPart = partOwner.find<MemoryPart>()

        ShadowCasting.computeFOV(currentPosition.x, currentPosition.y, visionRange,
            isBlocking = { x, y ->
                world[x, y]?.any { it.find<PhysicalPart>()?.doesBlockVision == true } ?: false
            }, markVisible = { x, y ->
                val visiblePosition = Position(x, y)
                nextVisiblePositions.add(visiblePosition)
                memoryPart?.remember(context, visiblePosition)
            })

        visiblePositions = nextVisiblePositions.toSet()

        if (canHavePlayerSense) {
            handlePlayerSense(context, partOwner)
        }
    }

    /**
     * Player sense allows an entity to see where the player is without having actual line of sight
     * (i.e., with shadow casting). This is useful for allowing an entity to chase a player that is just a little out
     * of sight. However, the entity should only be able to do this after sensing the player, i.e., having seen
     * the player.
     */
    private fun handlePlayerSense(context: Context, partOwner: Entity) {
        val currentPlayerPosition = context.positionOf(context.player) ?: return

        if (playerPosition != null) {
            playerPosition = currentPlayerPosition
            return
        }

        if (visiblePositions.contains(currentPlayerPosition)) {
            playerPosition = currentPlayerPosition
        }
    }

    private fun updateAsOmniscient(context: Context) {
        val world = context.world

        if (visiblePositions.size >= world.width * world.height) return

        visiblePositions = Position(world.width - 1, world.height - 1).map { it }.toSet()
    }
}

fun Entity.visiblePositions(): Set<Position> = find<SensoryPart>()?.visiblePositions ?: setOf()
