package apc.eagle.common.hero

import apc.eagle.common.*

@Suppress("unused")
class Hero508 : HeroType() { // 伽罗

    override val preferredIcon = 305082
    override val learn = intArrayOf(
        1, 2, 1, 3,
        1, 2, 1, 3,
        1, 2, 1, 3,
        2, 2, 2
    )

    override fun initAbilities() {
        if (attackAbilities.size == 1) attackAbilities += Ability("长弓", 1250, 1)
        super.initAbilities()
        abilities[0].run {
            type = Ability.TYPE_BUFF
            isSpell = false
        }
    }

    override fun updateSpecificAttributes(hero: Hero) {
        Wound.extraDamage = 43 + hero.abilityLevels[0] * 2
    }

    override fun onAction(actor: Hero, target: Hero) {
        when {
            actor.attackIndex != 1 && actor.abilityOnTime(0) -> doCast(actor, target, 0)
            actor.attackOnTime() -> doAttack(actor, target)
        }
    }

    override fun doAttack(actor: Hero, target: Hero) {
        super.doAttack(actor, target)
        if (actor.attackIndex == 1) actor.battle.events += Event(
            actor.battle.time + GameData.MS_FRAME * 5,
            target,
            actor,
            Wound
        )
    }

    override fun doCast(actor: Hero, target: Hero, index: Int) {
        super.doCast(actor, target, index)
        if (index == 0) actor.attackIndex = 1 - actor.attackIndex
    }
}

object Wound : Ability("损伤", TYPE_MAGIC) {
    init {
        attackFactor = 5
    }
}