package com.helloworldramen.kingoyster.worldgen.metadata

data class WorldFlavor(
    val wallFlavour: WallFlavor,
    val floorFlavor: FloorFlavor

) {

    companion object {
        val DRY_GRASS = WorldFlavor(WallFlavor.GrassStone, FloorFlavor.DryGrass)
        val LUSH_GRASS = WorldFlavor(WallFlavor.OldStone, FloorFlavor.LushGrass)
        val WOOD = WorldFlavor(WallFlavor.Wood, FloorFlavor.Wood)
    }
}