package com.helloworldramen.kingoyster.worldgen.metadata

import com.helloworldramen.kingoyster.entities.FeatureFactory
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn

sealed class WallFlavor(val wallFactoryFn: EntityFactoryFn) {
    object GrassStone : WallFlavor(FeatureFactory.wall("grass_stone_wall"))
    object OldStone : WallFlavor(FeatureFactory.wall("old_stone_wall"))
    object Wood : WallFlavor(FeatureFactory.wall("wood_wall"))
}