package com.helloworldramen.kingoyster.scenes.optionsmenu

import com.helloworldramen.kingoyster.scenes.autoload.audio.AudioAutoload
import com.helloworldramen.kingoyster.scenes.mainmenu.MainMenuScene
import com.helloworldramen.kingoyster.utilities.Settings
import godot.HSlider
import godot.InputEvent
import godot.MarginContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class OptionsMenuScene : MarginContainer() {

	private val audio: AudioAutoload by lazy { getNodeAs(AudioAutoload.TREE_PATH)!! }
	private val bgmSlider: HSlider by lazy { getNodeAs("$VBOX_PATH/BgmHBox/BgmSlider")!! }
	private val sfxSlider: HSlider by lazy { getNodeAs("$VBOX_PATH/SfxHBox/SfxSlider")!! }

	@RegisterFunction
	override fun _ready() {
		val bgmVolume = Settings.load("bgm_volume", "value", AudioAutoload.DEFAULT_BGM_VOLUME)
		val sfxVolume = Settings.load("sfx_volume", "value", AudioAutoload.DEFAULT_SFX_VOLUME)

		bgmSlider.value = bgmVolume * 10
		sfxSlider.value = sfxVolume * 10

		bgmSlider.valueChanged.connect(this, ::onBgmVolumeChanged)
		sfxSlider.valueChanged.connect(this, ::onSfxVolumeChanged)
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		when {
			event.isActionPressed("ui_cancel") -> {
				getTree()?.changeScene(MainMenuScene.PATH)
			}
		}
	}

	@RegisterFunction
	fun onBgmVolumeChanged(value: Double) {
		audio.bgmVolume = value / 10.0
	}

	@RegisterFunction
	fun onSfxVolumeChanged(value: Double) {
		audio.sfxVolume = value / 10.0
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/optionsmenu/OptionsMenuScene.tscn"

		private const val VBOX_PATH = "CenterContainer/VBox"
	}
}
