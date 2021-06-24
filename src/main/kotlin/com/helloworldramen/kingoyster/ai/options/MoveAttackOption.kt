package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.MoveAttack
import com.helloworldramen.kingoyster.actions.Telegraph
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.GameAiStrategy
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.parts.TelegraphInfo
import com.helloworldramen.kingoyster.parts.TelegraphPayload

class MoveAttackOption(
    override val parentStrategy: GameAiStrategy,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "mvk"

    override fun execute(): Boolean {
        val (context, entity, _, _, direction) = optionContext

        if (direction == null) return false

        val attackPattern = entity.find<MovementPart>()?.attackPattern ?: return false
        val telegraphedPositions = attackPattern.telegraphPositions(context, entity, direction)
        val payload = TelegraphPayload(MoveAttack(context, entity, direction), telegraphedPositions)
        val telegraphInfo = TelegraphInfo(entity, 1, listOf(payload))

        return entity.respondToAction(Telegraph(context, entity, telegraphInfo))
    }
}