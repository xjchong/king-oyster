package com.helloworldramen.kingoyster.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

object EventBus: CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    private val subscribersForEvent: MutableMap<KClass<out Event>, List<EventBusSubscriber>> = mutableMapOf()
    private val eventsForSubscriber: MutableMap<EventBusSubscriber, List<KClass<out Event>>> = mutableMapOf()

    fun register(subscriber: EventBusSubscriber, vararg events: KClass<out Event>) {
        eventsForSubscriber[subscriber] = events.toList()
        events.forEach { event ->
            subscribersForEvent[event] = subscribersForEvent[event]?.plus(listOf(subscriber)) ?: listOf(subscriber)
        }
    }

    fun unregister(subscriber: EventBusSubscriber) {
        eventsForSubscriber[subscriber]?.forEach {
            subscribersForEvent[it] = subscribersForEvent[it]?.filter { s -> s != subscriber } ?: listOf()
        }
        eventsForSubscriber.remove(subscriber)
    }

    fun post(event: Event) {
        subscribersForEvent[event::class]?.forEach {
//            launch { // Disabling this for now until I can figure out how to have UI respond to events in a thread safe way.
                it.receiveEvent(event)
//            }
        }
    }
}