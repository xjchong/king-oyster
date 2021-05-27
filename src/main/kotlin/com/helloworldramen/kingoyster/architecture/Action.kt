package com.helloworldramen.kingoyster.architecture

interface Action {
    val context: Context
    val actor: Entity
    val timeFactor: Double
}