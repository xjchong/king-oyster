package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.utilities.WeightedCollection

class AppearancePart(
    val description: String = "(no description)",
    val ascii: Char = '?',
    val color: String = "ffffff",
    val sprite: String? = null,
    val offset: Pair<Int, Int> = Pair(0, 0),
    val weightedFrameIndices: WeightedCollection<Long> = WeightedCollection(),
    val frameIndex: Long? = weightedFrameIndices.sample(), // Used for randomizing static sprite appearances.
    val isAnimated: Boolean = frameIndex == null
) : Part {

    override fun copy(): Part {
        return AppearancePart(description, ascii, color, sprite, offset, weightedFrameIndices, frameIndex, isAnimated)
    }
}

fun Entity.description(): String = find<AppearancePart>()?.description ?: "(no description)"