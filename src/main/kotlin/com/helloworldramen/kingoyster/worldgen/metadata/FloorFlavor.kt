package com.helloworldramen.kingoyster.worldgen.metadata

import com.helloworldramen.kingoyster.utilities.WeightedCollection

sealed class FloorFlavor(
    val sprite: String,
    val weightedFrameIndices: WeightedCollection<Long>
) {
    object DryGrass : FloorFlavor(
        sprite = "dry_grass_floor",
        weightedFrameIndices = STANDARD_FLOOR_WEIGHTS
    )

    object LushGrass : FloorFlavor(
        sprite = "lush_grass_floor",
        weightedFrameIndices = STANDARD_FLOOR_WEIGHTS
    )

    object Wood : FloorFlavor(
        sprite = "wood_floor",
        weightedFrameIndices = STANDARD_FLOOR_WEIGHTS
    )

    companion object {

        private val STANDARD_FLOOR_WEIGHTS: WeightedCollection<Long> = WeightedCollection(
            550 to 0, 30 to 1, 30 to 2, 30 to 3,
            25 to 4, 25 to 5, 25 to 6, 25 to 7,
            25 to 8, 25 to 9, 25 to 10, 25 to 11,
            25 to 12, 25 to 13, 25 to 14, 25 to 15,
            25 to 16, 25 to 17, 25 to 18, 25 to 19
        )
    }
}
