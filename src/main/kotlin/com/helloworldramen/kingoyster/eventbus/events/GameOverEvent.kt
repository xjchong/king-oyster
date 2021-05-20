package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.eventbus.Event

data class GameOverEvent(val isVictory: Boolean) : Event