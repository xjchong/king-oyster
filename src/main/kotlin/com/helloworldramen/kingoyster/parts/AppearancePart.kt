package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.utilities.WeightedCollection
import godot.core.Color
import godot.core.Vector2

class AppearancePart(
    val ascii: Char = '?',
    val color: Color = Color.white,
    val sprite: String? = null,
    val offset: Vector2 = Vector2.ZERO,
    val weightedFrameIndices: WeightedCollection<Int> = WeightedCollection(),
    val frameIndex: Int? = weightedFrameIndices.sample(), // Used for randomizing static sprite appearances.
    val isAnimated: Boolean = frameIndex == null
) : Part {

    override fun copy(): Part {
        return AppearancePart(ascii, color, sprite, offset, weightedFrameIndices, frameIndex, isAnimated)
    }
}