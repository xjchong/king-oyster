package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Telegraph
import com.helloworldramen.kingoyster.actions.WeaponAttack
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext
import com.helloworldramen.kingoyster.parts.TelegraphInfo
import com.helloworldramen.kingoyster.parts.TelegraphPayload
import com.helloworldramen.kingoyster.parts.combat.defaultAttackPattern
import com.helloworldramen.kingoyster.parts.weapon
import com.helloworldramen.kingoyster.parts.weaponAttackPattern

class WeaponAttackOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "wak"

    override fun execute(): Boolean {
        val (context, entity, _, _, direction) = optionContext

        if (direction == null) return false

        val attackPattern = entity.weapon()?.weaponAttackPattern() ?: entity.defaultAttackPattern()
        val positions = attackPattern.calculateDamageForPosition(context, entity, direction).keys.toList()
        val payload = TelegraphPayload(WeaponAttack(context, entity, direction), positions)
        val telegraphInfo = TelegraphInfo(entity, 1, listOf(payload))

        return entity.respondToAction(Telegraph(context, entity, telegraphInfo))
    }
}