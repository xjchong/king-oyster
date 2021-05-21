package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Part

class FactionPart(
    val faction: Faction
) : Part {

    override fun copy(): Part {
        return FactionPart(faction)
    }
}

sealed class Faction {
    object Player : Faction()
    object Monster : Faction()
}
