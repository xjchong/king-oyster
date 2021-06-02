package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.EquipAsWeapon
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext

class EquipWeaponOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "eqw"

    override fun execute(): Boolean {
        val (context, entity, weapon) = optionContext

        if (weapon == null) return false
        if (context.positionOf(entity) != context.positionOf(weapon)) return false

        return weapon.respondToAction(EquipAsWeapon(context, entity))
    }
}