package com.helloworldramen.kingoyster.utilities

import com.helloworldramen.kingoyster.worldgen.metadata.WorldFlavor
import godot.ConfigFile
import godot.core.Color
import godot.core.GodotError
import godot.core.memory.GodotStatic

object Settings : GodotStatic {

    val BACKGROUND_COLOR = Color(0.02, 0.12, 0.12)

    private const val CONFIG_PATH = "user://settings.cfg"
    private val configFile = ConfigFile().apply { load(CONFIG_PATH) }

    override fun collect() {
        configFile.free()
    }

    fun save(section: String, key: String, value: Any?): Boolean {
        configFile.setValue(section, key, value)

        return configFile.save(CONFIG_PATH) == GodotError.OK
    }

    fun <T : Any>load(section: String, key: String, defaultValue: T): T {
        return configFile.getValue(section, key, defaultValue) as? T ?: defaultValue
    }
}