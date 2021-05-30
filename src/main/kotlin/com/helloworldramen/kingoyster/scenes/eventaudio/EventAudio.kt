package com.helloworldramen.kingoyster.scenes.eventaudio

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.entities.isPlayer
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.*
import com.helloworldramen.kingoyster.scenes.autoload.audio.AudioAutoload
import com.helloworldramen.kingoyster.scenes.autoload.audio.SFX
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class EventAudio : Node(), EventBusSubscriber {

	private val audio: AudioAutoload by lazy { getNodeAs(AudioAutoload.TREE_PATH)!! }

	private var context: Context = Context.UNKNOWN

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this,
			AscendEvent::class,
			AttackEvent::class,
			DamageEvent::class,
			GameOverEvent::class,
			MoveEvent::class,
			TakeEvent::class
		)
	}

	@RegisterFunction
	override fun _onDestroy() {
		EventBus.unregister(this)
	}

	override fun receiveEvent(event: Event) {
		when(event) {
			is AscendEvent -> onAscend(event)
			is AttackEvent -> onAttack(event)
			is DamageEvent -> onDamage(event)
			is DeathEvent -> onDeath(event)
			is GameOverEvent -> onGameOver(event)
			is MoveEvent -> onMove(event)
			is TakeEvent -> onTake(event)
		}
	}

	fun bind(context: Context) {
		this.context = context
	}

	private fun onAscend(event: AscendEvent) {
		audio.play(SFX.STAIRS)
	}

	private fun onAttack(event: AttackEvent) {
		if (event.attacker.isPlayer) {
			audio.play(SFX.HIT_BASH)
		}
	}

	private fun onDamage(event: DamageEvent) {
	}

	private fun onDeath(event: DeathEvent) {
	}

	private fun onGameOver(event: GameOverEvent) {
	}

	private fun onMove(event: MoveEvent) {
		if (event.entity.isPlayer) {
			audio.play(SFX.MOVE_STEP)
		}
	}

	private fun onTake(event: TakeEvent) {

	}
}
