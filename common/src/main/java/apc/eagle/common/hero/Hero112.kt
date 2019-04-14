package apc.eagle.common.hero

import apc.eagle.common.*

@Suppress("unused")
class Hero112 : HeroType() { // 鲁班七号

    override val preferredIcon = 301124
    override val specialAttackName = StrafeMob.name
    override val learn = intArrayOf(
        1, 2, 2, 3,
        2, 1, 2, 3,
        2, 1, 2, 3,
        1, 1, 1
    )

    override fun initAbilities() {
        if (attackAbilities.size == 1) attackAbilities += Strafe
        super.initAbilities()
    }

    override fun updateSpecificAttributes(hero: Hero) {
        StrafeMob.extraDamage = 112 + hero.level * 8
        hero.avgAttack[0] = hero.avgAttack()
        hero.avgAttack[1] = hero.avgAttack(StrafeMob)
    }

    override fun attackFrames(haste: Int, index: Int) =
        if (index == 1) 26 - attackAbilities[1].speeds.indexOfLast { haste >= it } else super.attackFrames(haste, index)

    override fun onAction(actor: Hero, target: Hero) {
        if (!actor.channeling) {
            when {
                actor.heroRage >= 3 && actor.attackOnTime() -> doAttack(actor, target)
                actor.abilityOnTime(0) -> doCast(actor, target, 0)
                actor.abilityOnTime(2) -> doCast(actor, target, 2)
                actor.abilityOnTime(1) -> doCast(actor, target, 1)
                actor.attackOnTime() -> doAttack(actor, target)
            }
        }
    }

    override fun doAttack(actor: Hero, target: Hero) {
        val time = target.battle.time
        actor.nextAttackTime = time + attackFrames(actor.haste) * GameData.MS_FRAME
        if (actor.heroRage >= 4) {
            actor.heroRage = 0
            val duration = (attackFrames(actor.haste, 1) - 1) * GameData.MS_FRAME
            actor.channel(duration, Strafe)
            val first = time + GameData.MS_FRAME
            actor.battle.events += Event(first, target, actor, Strafe)
            actor.battle.events += Event(first + duration / 3, target, actor, Strafe)
            actor.battle.events += Event(first + duration * 2 / 3, target, actor, Strafe)
        } else {
            ++actor.heroRage
            actor.battle.events += Event(time + GameData.MS_FRAME * 5, target, actor)
        }
    }

    override fun doCast(actor: Hero, target: Hero, index: Int) {
        actor.heroRage += 4
        val time = target.battle.time
        val ability = abilities[index]
        val cd = (ability.cd + ability.bonusCd * (actor.abilityLevels[index] - 1)) * (1000 - actor.cdr) / 1000
        actor.nextCastTime[index] = time + cd
        if (actor.enchant != null && time >= actor.nextEnchantTime) {
            actor.nextEnchantTime = time + 2000
            actor.enchanting = true
        }
        actor.battle.logs += Event(actor, ability)
    }
}

object Strafe : Ability("扫射", TYPE_PHYSICAL) {
    init {
        speeds = intArrayOf(46, 76, 122, 182, 228, 304, 364, 440, 532, 622, 728, 850, 986, 1158, 1350, 1592, 1864)
        type = TYPE_PHYSICAL
        canOrb = true
        canCritical = true
        channel = true
    }

    override fun off(hero: Hero, stacks: Int) {
        hero.channeling = false
        hero.battle.events.removeAll { it.ability == this && it.target != hero && it.attacker == hero }
    }

    override fun damage(attacker: Hero, target: Hero, abilityFactor: Int): Int {
        var damage = target.mhp * (6 + attacker.extraAttack / 100) / 100
        var factor = defenseFactor(attacker, target)
        if (attacker.hasExecute && target.hp < target.mhp * 50 / 100) factor *= 1.3f
        damage = attacker.criticalDamage(damage, factor)
        return (damage * factor).toInt()
    }
}

object StrafeMob : Ability("对怪扫射", TYPE_PHYSICAL) {
    init {
        attackFactor = 50
    }
}