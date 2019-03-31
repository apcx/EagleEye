package apc.eagle.common.hero

import apc.eagle.common.Ability
import apc.eagle.common.HeroType

@Suppress("unused")
class Hero192 : HeroType() { // 黄忠

    override fun initSpeeds() {
        if (attackAbilities.size == 1) attackAbilities += Ability("炮击", 1300, 1)
        super.initSpeeds()
    }
}