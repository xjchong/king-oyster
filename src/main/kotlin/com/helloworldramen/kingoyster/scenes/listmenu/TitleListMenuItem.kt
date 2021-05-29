package com.helloworldramen.kingoyster.scenes.listmenu

import godot.HBoxContainer
import godot.Label
import godot.annotation.RegisterClass
import godot.extensions.getNodeAs

@RegisterClass
class TitleListMenuItem : HBoxContainer() {

	private val selector: Label by lazy { getNodeAs("HBox/Selector")!! }
	private val label: Label by lazy { getNodeAs("HBox/Label")!! }

	fun bind(title: String) {
		label.text = title
	}

	fun select() {
		selector.text = ">"
	}

	fun deselect() {
		selector.text = ""
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/listmenu/TitleListMenuItem.tscn"
	}
}
