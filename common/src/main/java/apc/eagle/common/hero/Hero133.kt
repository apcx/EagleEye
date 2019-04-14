package apc.eagle.common.hero

import apc.eagle.common.*

@Suppress("unused")
class Hero133 : HeroType() { // 狄仁杰

    override val preferredIcon = 301333
    override val passiveHaste = 300
    override val learn = intArrayOf(
        1, 2, 1, 3,
        1, 2, 1, 3,
        1, 2, 1, 3,
        2, 2, 2
    )

    override fun updateSpecificAttributes(hero: Hero) {
        val level = hero.abilityLevels[0]
        RedCard.extraDamage = 144 + level * 16
        BlueCard.extraDamage = RedCard.extraDamage / 2
    }

    override fun doAttack(actor: Hero, target: Hero) {
        super.doAttack(actor, target)
        actor.addBuff(Haste)
        ++actor.heroRage
        val rage = actor.heroRage
        if (rage % 3 == 0) {
            val card = if (rage % 6 == 0) RedCard else BlueCard
            actor.battle.events += Event(actor.battle.time + GameData.MS_FRAME * 5, target, actor, card)
        }
    }
}

object RedCard : Ability("红牌", TYPE_MAGIC)
object BlueCard : Ability("蓝牌", TYPE_MAGIC)
object Haste : Ability("迅捷", TYPE_BUFF) {

    init {
        duration = 2000
        maxStacks = 5
    }

    override fun on(hero: Hero) {
        hero.baseHaste += 60
    }

    override fun off(hero: Hero, stacks: Int) {
        hero.baseHaste -= 60 * stacks
    }
}