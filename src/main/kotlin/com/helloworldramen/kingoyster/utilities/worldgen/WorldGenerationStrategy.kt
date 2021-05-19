package com.helloworldramen.kingoyster.utilities.worldgen

import com.helloworldramen.kingoyster.oyster.World

interface WorldGenerationStrategy {

    fun generate(world: World)
}