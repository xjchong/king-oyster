package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Part

class MoneyPart(val value: Int) : Part {

    override fun copy(): Part {
        return MoneyPart(value)
    }
}