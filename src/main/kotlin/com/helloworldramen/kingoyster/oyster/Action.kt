package com.helloworldramen.kingoyster.oyster

interface Action {
    val context: Context
    val actor: Entity
    val timeFactor: Double
}