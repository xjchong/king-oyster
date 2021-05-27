package com.helloworldramen.kingoyster.utilities.worldgen

import com.helloworldramen.kingoyster.architecture.World

interface WorldGenerationStrategy {

    fun generate(world: World)
}