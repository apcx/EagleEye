package apc.eagle.common.hero

import apc.eagle.common.HeroType

@Suppress("unused")
class Hero169 : HeroType() {
    override fun attackFrames(speed: Int) = if (speed < 30) 16 else 12 - speeds.indexOfLast { speed >= it }
}