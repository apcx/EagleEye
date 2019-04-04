package apc.eagle.common.hero

import apc.eagle.common.HeroType

@Suppress("unused")
class Hero169 : HeroType() { // 后羿
    override fun passiveSpeed(level: Int) = 300
    override fun attackFrames(speed: Int, index: Int) =
        if (speed < 30) 16 else 12 - attackAbilities[0].speeds.indexOfLast { speed >= it }
}