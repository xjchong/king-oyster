package com.helloworldramen.kingoyster.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object EventBus: CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    private val subscribersForEvent: MutableMap<Event, MutableList<EventBusSubscriber>> = mutableMapOf()
    private val eventsForSubscriber: MutableMap<EventBusSubscriber, List<Event>> = mutableMapOf()

    fun register(subscriber: EventBusSubscriber, vararg events: Event) {
        eventsForSubscriber[subscriber] = events.toList()
    }

    fun unregister(subscriber: EventBusSubscriber) {
        eventsForSubscriber[subscriber]?.forEach {
            subscribersForEvent[it]?.remove(subscriber)
        }
        eventsForSubscriber.remove(subscriber)
    }

    fun post(event: Event) {
        subscribersForEvent[event]?.forEach {
            launch {
                it.receiveEvent(event)
            }
        }
    }
}