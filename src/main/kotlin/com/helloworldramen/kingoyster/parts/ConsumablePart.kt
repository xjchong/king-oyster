package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Part

class ConsumablePart : Part {

    override fun copy(): Part {
        return ConsumablePart()
    }
}