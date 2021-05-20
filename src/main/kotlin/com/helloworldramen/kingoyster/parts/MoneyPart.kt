package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Part

class MoneyPart(val value: Int) : Part {

    override fun copy(): Part {
        return MoneyPart(value)
    }
}