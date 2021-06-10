package com.helloworldramen.kingoyster.ai.options

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.ai.GameAiOption
import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.ai.architecture.AiStrategy
import com.helloworldramen.kingoyster.ai.architecture.AiStrategyContext
import com.helloworldramen.kingoyster.ai.tag
import com.helloworldramen.kingoyster.utilities.pathing.Pathing

class PathCloserOption(
    override val parentStrategy: AiStrategy<out AiStrategyContext, GameAiOptionContext>,
    override val optionContext: GameAiOptionContext
) : GameAiOption() {

    override val tag: String = "ptc.${optionContext.position.tag()}"

    override fun execute(): Boolean {
        val (context, entity, _, position) = optionContext

        if (position == null) return false

        val nextPosition = Pathing.pathTo(context, entity, position) ?: return false

        return entity.respondToAction(Move(context, entity, nextPosition))
    }
}