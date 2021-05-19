package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Part

class PhysicalPart(
    var isPassable: Boolean = true,
    var doesBlockVision: Boolean = false
) : Part {
}