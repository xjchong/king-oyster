package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Telegraph
import com.helloworldramen.kingoyster.actions.UseAbility
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext
import com.helloworldramen.kingoyster.parts.TelegraphInfo
import com.helloworldramen.kingoyster.parts.TelegraphPayload
import com.helloworldramen.kingoyster.parts.combat.ability

class UseAbilityOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "abl"

    override fun execute(): Boolean {
        val (context, entity) = optionContext
        val ability = entity.ability() ?: return false
        val telegraphedPositions = ability.telegraphPositions(context, entity, entity)
        val payload = TelegraphPayload(UseAbility(context, entity), telegraphedPositions)
        val telegraphInfo = TelegraphInfo(entity, 1, listOf(payload))

        return entity.respondToAction(Telegraph(context, entity, telegraphInfo))
    }
}