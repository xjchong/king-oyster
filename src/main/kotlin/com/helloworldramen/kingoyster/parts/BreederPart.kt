package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Breed
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.BreedEvent
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn

class BreederPart(var maxChildCount: Int, val entityFactoryFn: EntityFactoryFn) : Part {

    private var childCount: Int = 0

    private constructor(childCount: Int, maxChildCount: Int, entityFactoryFn: EntityFactoryFn)
            : this(maxChildCount, entityFactoryFn) {
        this.childCount = childCount
    }

    override fun copy(): Part {
        return BreederPart(childCount, maxChildCount, entityFactoryFn)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is Breed -> partOwner.respondToBreed(action)
            else -> false
        }
    }

    private fun Entity.respondToBreed(action: Breed): Boolean {
        val (context, actor) = action

        if (actor != this) return false
        if (childCount >= maxChildCount) return false

        val currentPosition = context.positionOf(this) ?: return false
        val breedPosition = currentPosition.neighborsShuffled().firstOrNull { neighbor ->
            context.entitiesAt(neighbor)?.all { it.isPassable() } == true
        } ?: return false
        val child = entityFactoryFn()

        child.find<BreederPart>()?.run {
            maxChildCount = this@BreederPart.maxChildCount - 1
        }

        return if (context.world.add(child, breedPosition)) {
            childCount++
            EventBus.post(BreedEvent(actor, child))
            true
        } else {
            false
        }
    }
}