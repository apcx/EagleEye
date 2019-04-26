package apc.eagle.common.hero

import apc.eagle.common.Hero
import apc.eagle.common.HeroType

@Suppress("unused")
class Hero196 : HeroType() { // 百里守约

    override val preferredIcon = 301964

    override fun initAbilities() {
        super.initAbilities()
        attackAbilities[0].run {
            attackFactor = 180
            canCritical = false
        }
    }

    override fun updateSpecificAttributes(hero: Hero) {
        hero.extraAttack += hero.critical * 3 / 10
    }
}