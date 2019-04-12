package apc.eagle.common.hero

import apc.eagle.common.Ability
import apc.eagle.common.Hero
import apc.eagle.common.HeroType

@Suppress("unused")
class Hero192 : HeroType() { // 黄忠

    override val learn = intArrayOf(
        2, 1, 1, 3,
        1, 2, 1, 3,
        1, 2, 1, 3,
        2, 2, 2
    )

    override fun initAbilities() {
        if (attackAbilities.size == 1) attackAbilities += Ability("炮击", 1300, 1)
        super.initAbilities()
    }

    override fun updateSpecificAttributes(hero: Hero) {
        val level = hero.abilityLevels[0]
        if (level > 0) hero.extraAttack += 54 + level * 6
    }
}

object BurnSoul : Ability("炮手燃魂", TYPE_BUFF) {

    init {
        duration = 1_500
        maxStacks = 5
    }

    override fun on(hero: Hero) {
        hero.critical += 15
        hero.extraAttack += 8
    }

    override fun off(hero: Hero, stacks: Int) {
        hero.critical -= 15 * stacks
        hero.extraAttack -= 8 * stacks
    }
}