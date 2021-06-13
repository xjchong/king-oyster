package com.helloworldramen.kingoyster.worldgen.metadata

data class WorldFlavor(
    val wallFlavour: WallFlavor,
    val floorFlavor: FloorFlavor,
    val backgroundColor: String

) {

    companion object {
        val DRY_GRASS = WorldFlavor(WallFlavor.GrassStone, FloorFlavor.DryGrass, "091e05")
        val LUSH_GRASS = WorldFlavor(WallFlavor.OldStone, FloorFlavor.LushGrass, "051e1e")
        val WOOD = WorldFlavor(WallFlavor.Wood, FloorFlavor.Wood, "1e1005")

        val DEFAULT = LUSH_GRASS
    }
}