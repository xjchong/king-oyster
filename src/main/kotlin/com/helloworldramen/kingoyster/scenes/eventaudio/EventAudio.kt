package com.helloworldramen.kingoyster.scenes.eventaudio

import com.helloworldramen.kingoyster.actions.MoveType
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.entities.isPlayer
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.*
import com.helloworldramen.kingoyster.parts.visiblePositions
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
	private val player: Entity
		get() = context.player

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this,
			AscendEvent::class,
			AttackEvent::class,
			DamageEvent::class,
			DoorEvent::class,
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
		when (event) {
			is AscendEvent -> onAscend(event)
			is AttackEvent -> onAttack(event)
			is DamageEvent -> onDamage(event)
			is DeathEvent -> onDeath(event)
			is DoorEvent -> onDoor(event)
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
	}

	private fun onDamage(event: DamageEvent) {
		if (!event.target.isVisibleToPlayer()) return

		audio.play(SFX.HIT_BASH)
	}

	private fun onDeath(event: DeathEvent) {
	}

	private fun onDoor(event: DoorEvent) {
		if (!event.actor.isVisibleToPlayer()) return

		audio.play(if (event.isOpen) SFX.DOOR_OPEN else SFX.DOOR_CLOSE)
	}

	private fun onGameOver(event: GameOverEvent) {
	}

	private fun onMove(event: MoveEvent) {
		if (event.entity.isPlayer.not()) return

		when (event.type) {
			MoveType.Charge -> audio.play(SFX.HIT_CUT_CRIT)
			MoveType.Default -> audio.play(SFX.MOVE_STEP)
		}
	}

	private fun onTake(event: TakeEvent) {

	}

	private fun Entity.isVisibleToPlayer(): Boolean {
		return player.visiblePositions().contains(context.positionOf(this))
	}
}
