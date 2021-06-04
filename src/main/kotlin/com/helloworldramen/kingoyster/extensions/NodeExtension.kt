package com.helloworldramen.kingoyster.extensions

import godot.Node

fun Node.freeChildren() {
    getChildren().forEach {
        removeChild(it as Node)
        it.queueFree()
    }
}