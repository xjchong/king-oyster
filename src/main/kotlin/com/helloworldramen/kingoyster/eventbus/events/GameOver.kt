package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.eventbus.Event

data class GameOver(val isVictory: Boolean) : Event