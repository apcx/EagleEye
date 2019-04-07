package apc.eagle.common.hero

import apc.eagle.common.HeroType

@Suppress("unused")
class Hero196 : HeroType() { // 百里守约
    override fun initAbilities() {
        super.initAbilities()
        attackAbilities[0].canCritical = false
    }
}