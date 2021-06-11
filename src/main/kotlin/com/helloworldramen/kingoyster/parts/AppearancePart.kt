package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import godot.core.Color
import godot.core.Vector2

class AppearancePart(
    val description: String = "(no description)",
    val ascii: Char = '?',
    val color: Color = Color.white,
    val sprite: String? = null,
    val offset: Vector2 = Vector2.ZERO,
    val weightedFrameIndices: WeightedCollection<Long> = WeightedCollection(),
    val frameIndex: Long? = weightedFrameIndices.sample(), // Used for randomizing static sprite appearances.
    val isAnimated: Boolean = frameIndex == null
) : Part {

    override fun copy(): Part {
        return AppearancePart(description, ascii, color, sprite, offset, weightedFrameIndices, frameIndex, isAnimated)
    }
}

fun Entity.description(): String = find<AppearancePart>()?.description ?: "(no description)"