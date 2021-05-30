package com.helloworldramen.kingoyster.scenes.autoload.audio

import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.extensions.getNodeAs
import godot.global.GD
import java.util.*

@RegisterClass
class AudioAutoload : Node() {

	var bgmVolume: Double = 0.0
		set(value) { field = updateVolume(BGM_BUS_ID, value) }

	var sfxVolume: Double = 0.0
		set(value) { field = updateVolume(SFX_BUS_ID, value) }

	private val bgmAudioPlayer: AudioStreamPlayer by lazy { getNodeAs("BGMAudioPlayer")!! }
	private val bgmTween: Tween by lazy { getNodeAs("BGMAudioPlayer/Tween")!! }
	private val sfxAudioPlayers: MutableList<AudioStreamPlayer> = mutableListOf()
	private val sfxPathQueue: Queue<String> = ArrayDeque()

	private var lastBgmVolume: Double = DEFAULT_BGM_VOLUME

	@RegisterFunction
	override fun _ready() {
		pauseMode = PAUSE_MODE_PROCESS

		// TODO: Load volume settings from settings manager.
		updateVolume(BGM_BUS_ID, DEFAULT_BGM_VOLUME)
		updateVolume(SFX_BUS_ID, DEFAULT_SFX_VOLUME)
		setupAudioPlayers()

		bgmTween.tweenAllCompleted.connect(this, ::onBGMTweenAllCompleted)
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		if (sfxPathQueue.isEmpty()) return

		sfxAudioPlayers.firstOrNull { !it.playing }?.let { audioPlayer ->
			audioPlayer.stream = GD.load(sfxPathQueue.poll())
			audioPlayer.play()
		}
	}

	@RegisterFunction
	fun onBGMTweenAllCompleted() {
		with(bgmAudioPlayer) {
			stop()
			stream = null
			volumeDb = lastBgmVolume
		}
	}

	fun playSfx(sfxPath: String) {
		sfxPathQueue.offer(sfxPath)
	}

	/**
	 * Note that the related audio resource should be set to loop.
	 */
	fun playBgm(bgmPath: String) {
		val audioResource = GD.load<AudioStream>(bgmPath)

		if (bgmAudioPlayer.stream == audioResource) return

		bgmAudioPlayer.stream = audioResource
		bgmAudioPlayer.play()
	}

	fun stopBgm() {
		if (!bgmAudioPlayer.playing) return

		lastBgmVolume = bgmAudioPlayer.volumeDb

		bgmTween.interpolateProperty(
			bgmAudioPlayer, NodePath("volume_db"),
			initialVal = bgmAudioPlayer.volumeDb,
			finalVal = -80.0,
			duration = 4.0
		)
		bgmTween.start()
	}

	private fun updateVolume(busId: Long, volume: Double): Double {
		with(volume.coerceIn(0.0, 1.0)) {
			AudioServer.setBusVolumeDb(busId, GD.linear2db(this))
			return this
		}
	}

	private fun setupAudioPlayers() {
		repeat(MAX_AUDIO_PLAYERS) {
			val audioPlayer = AudioStreamPlayer().apply {
				bus = SFX_BUS_ID.toString()
			}

			addChild(audioPlayer)
			sfxAudioPlayers.add(audioPlayer)
		}
	}

	companion object {
		private const val DEFAULT_SFX_VOLUME = 0.7
		private const val DEFAULT_BGM_VOLUME = 0.6
		private const val MAX_AUDIO_PLAYERS = 12
		private const val BGM_BUS_ID: Long = 1
		private const val SFX_BUS_ID: Long = 2
	}
}
