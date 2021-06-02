package com.helloworldramen.kingoyster.scenes.eventaudio

import com.helloworldramen.kingoyster.actions.MoveType
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.entities.isPlayer
import com.helloworldramen.kingoyster.entities.isVisibleToPlayer
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
			WeaponAttackEvent::class,
			DamageEvent::class,
			DeathEvent::class,
			DoorEvent::class,
			EquipWeaponEvent::class,
			GameOverEvent::class,
			MoveEvent::class,
			TakeEvent::class,
			ThrowWeaponEvent::class
		)
	}

	@RegisterFunction
	override fun _onDestroy() {
		EventBus.unregister(this)
	}

	override fun receiveEvent(event: Event) {
		when (event) {
			is AscendEvent -> onAscend(event)
			is WeaponAttackEvent -> onAttack(event)
			is DamageEvent -> onDamage(event)
			is DeathEvent -> onDeath(event)
			is DoorEvent -> onDoor(event)
			is EquipWeaponEvent -> onEquipWeapon(event)
			is GameOverEvent -> onGameOver(event)
			is MoveEvent -> onMove(event)
			is TakeEvent -> onTake(event)
			is ThrowWeaponEvent -> onThrowWeapon(event)
		}
	}

	fun bind(context: Context) {
		this.context = context
	}

	private fun onAscend(event: AscendEvent) {
		audio.play(SFX.STAIRS)
	}

	private fun onAttack(event: WeaponAttackEvent) {
	}

	private fun onDamage(event: DamageEvent) {
		if (!event.target.isVisibleToPlayer(context)) return

		audio.play(SFX.HIT_BASH)
	}

	private fun onDeath(event: DeathEvent) {
		if (!event.entity.isVisibleToPlayer(context)) return

		audio.play(SFX.DEATH)
	}

	private fun onDoor(event: DoorEvent) {
		if (!event.actor.isVisibleToPlayer(context)) return

		audio.play(if (event.isOpen) SFX.DOOR_OPEN else SFX.DOOR_CLOSE)
	}

	private fun onEquipWeapon(event: EquipWeaponEvent) {
		if (!event.equipper.isVisibleToPlayer(context)) return

		audio.play(SFX.TAKE)
	}

	private fun onGameOver(event: GameOverEvent) {
	}

	private fun onMove(event: MoveEvent) {
		if (!event.entity.isPlayer) return

		when (event.type) {
			MoveType.Charge -> audio.play(SFX.HIT_SWOOSH_CUT)
			MoveType.Default -> audio.play(SFX.MOVE_STEP)
		}
	}

	private fun onTake(event: TakeEvent) {
		if (!event.taker.isVisibleToPlayer(context)) return

		audio.play(SFX.TAKE)
	}

	private fun onThrowWeapon(event: ThrowWeaponEvent) {
		if (!event.thrower.isVisibleToPlayer(context)) return

		audio.play(SFX.HIT_CUT_CRIT)
	}
}
