package apc.eagle.common.hero

import apc.eagle.common.Ability
import apc.eagle.common.HeroType

@Suppress("unused")
class Hero508 : HeroType() { // 伽罗
    override fun initSpeeds() {
        if (attackAbilities.size == 1) attackAbilities += Ability("长弓", 1250, 1)
        super.initSpeeds()
    }
}