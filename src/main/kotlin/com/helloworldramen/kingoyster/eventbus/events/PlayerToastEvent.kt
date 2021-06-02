package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.eventbus.Event
import godot.core.Color

data class PlayerToastEvent(
    val message: String,
    val color: Color
) : Event