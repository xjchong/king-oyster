package com.helloworldramen.kingoyster.eventbus

interface EventBusSubscriber {

    fun receiveEvent(event: Event)
}