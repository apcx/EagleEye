package apc.eagle.common.hero

import apc.eagle.common.*

@Suppress("unused")
class Hero169 : HeroType() { // 后羿

    override val preferredIcon = 301694
    override val passiveSpeed = 300
    override val specialAttackName get() = abilities[0].name
    override val learn = intArrayOf(
        2, 1, 1, 3,
        1, 2, 1, 3,
        1, 2, 1, 3,
        2, 2, 2
    )

    override fun initAbilities() {
        super.initAbilities()
        abilities[0].run {
            type = Ability.TYPE_BUFF
            duration = 5000
        }
        abilities[1].attackFactor = 50
        abilities[2].attackFactor = 90
    }

    override fun updateSpecificAttributes(hero: Hero) {
        val level = hero.abilityLevels[0]
        MultipleShot.extraDamage = 160 + level * 40
        abilities[1].extraDamage = 360 + hero.abilityLevels[1] * 40
        abilities[2].extraDamage = 525 + hero.abilityLevels[2] * 175
        hero.avgAttack[0] = hero.avgAttack()
        hero.avgAttack[1] = if (level <= 0) 0 else hero.avgAttack(MultipleShot)
    }

    override fun onAction(actor: Hero, target: Hero) {
        when {
//            actor.abilityOnTime(2) -> doCast(actor, target, 2)
            actor.attackOnTime() -> doAttack(actor, target)
            actor.avgAttack[1] > actor.avgAttack[0] && actor.abilityOnTime(0) -> doCast(actor, target, 0)
//            actor.abilityOnTime(1) -> doCast(actor, target, 1)
        }
    }

    override fun doAttack(actor: Hero, target: Hero) {
        val time = target.battle.time
        actor.nextAttackTime = time + attackFrames(actor.attackSpeed) * GameData.MS_FRAME
        val ability = if (actor.buff(abilities[0]) == null) attackAbilities[0] else MultipleShot
        val retribution = actor.buff(RetributionShot)
        if (retribution != null && retribution.stacks >= RetributionShot.maxStacks) {
            actor.battle.events += Event(time + GameData.MS_FRAME * 2, target, actor, ability, 40)
            actor.battle.events += Event(time + GameData.MS_FRAME * 4, target, actor, ability, 40)
            actor.battle.events += Event(time + GameData.MS_FRAME * 5, target, actor, ability, 40)
        } else {
            actor.battle.events += Event(time + GameData.MS_FRAME * 2, target, actor, ability)
        }
    }
}

object RetributionShot : Ability("惩戒射击", TYPE_BUFF) {

    init {
        duration = 3000
        maxStacks = 3
    }

    override fun on(hero: Hero) {
        hero.baseAttackSpeed += 100
    }

    override fun off(hero: Hero, stacks: Int) {
        hero.baseAttackSpeed -= 100 * stacks
    }
}

object MultipleShot : Ability("多重箭", TYPE_PHYSICAL) {
    init {
        attackFactor = 50
        canExpertise = true
        canOrb = true
        canCritical = true
    }
}