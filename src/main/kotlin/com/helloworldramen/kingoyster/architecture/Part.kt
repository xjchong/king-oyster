package com.helloworldramen.kingoyster.architecture

interface Part {

    fun copy(): Part

    fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return false
    }

    fun update(context: Context, partOwner: Entity) {

    }
}