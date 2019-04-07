package apc.eagle.common.hero

import apc.eagle.common.Ability
import apc.eagle.common.HeroType

@Suppress("unused")
class Hero199 : HeroType() { // 公孙离
    override fun initAbilities() {
        if (attackAbilities.size == 1) attackAbilities += Ability("速射", 800, 2)
        super.initAbilities()
    }
}