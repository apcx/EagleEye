package apc.eagle.common.hero

import apc.eagle.common.*

@Suppress("unused")
class Hero174 : HeroType() { // 虞姬

    override val preferredIcon = 301742
    override fun tempHaste(level: Int) = if (level >= 4) 200 + level / 4 * 100 else 0
    override val tempHasteName get() = abilities[2].name
    override val learn = intArrayOf(
        2, 1, 2, 3,
        2, 1, 2, 3,
        2, 1, 2, 3,
        1, 1, 1
    )

    override fun updateSpecificAttributes(hero: Hero) {
        ExtraArrow.extraDamage = 95 + hero.level * 5
        val level = hero.abilityLevels[1]
        if (level > 0) hero.baseHaste += 180 + level * 20
    }

    override fun doAttack(actor: Hero, target: Hero) {
        super.doAttack(actor, target)
        actor.heroRage += 25
        if (actor.heroRage >= 100) {
            actor.heroRage -= 100
            actor.battle.events += Event(target.battle.time + GameData.MS_FRAME * 7, target, actor, ExtraArrow)
        }
    }
}

object ExtraArrow : Ability("小箭", TYPE_PHYSICAL) {
    init {
        attackFactor = 20
        canExpertise = true
        canOrb = true
    }
}