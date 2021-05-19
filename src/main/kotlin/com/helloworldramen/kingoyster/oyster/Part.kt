package com.helloworldramen.kingoyster.oyster

interface Part {

    fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return false
    }

    fun update(context: Context) {

    }
}