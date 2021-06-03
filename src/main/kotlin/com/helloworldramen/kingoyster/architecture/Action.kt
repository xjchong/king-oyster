package com.helloworldramen.kingoyster.architecture

interface Action {
    val context: Context
    val world: World
    val actor: Entity
    val timeFactor: Double
}