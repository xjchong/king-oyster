package com.helloworldramen.kingoyster.eventbus.events

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.parts.Telegraph

data class TelegraphEvent(
    val entity: Entity,
    val telegraphs: List<Telegraph>
) : Event