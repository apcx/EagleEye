package apc.eagle.common.hero

import apc.eagle.common.Ability
import apc.eagle.common.HeroType

@Suppress("unused")
class Hero112 : HeroType() { // 鲁班七号
    override fun initAbilities() {
        if (attackAbilities.size == 1) {
            val ability = Ability("扫射", 1700, 1)
            ability.speeds =
                intArrayOf(46, 76, 122, 182, 228, 304, 364, 440, 532, 622, 728, 850, 986, 1158, 1350, 1592, 1864)
            attackAbilities += ability
        }
        super.initAbilities()
    }

    override fun attackFrames(speed: Int, index: Int) =
        if (index == 1) 26 - attackAbilities[1].speeds.indexOfLast { speed >= it } else super.attackFrames(speed, index)
}