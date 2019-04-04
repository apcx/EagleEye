package apc.eagle.common.hero

import apc.eagle.common.Ability
import apc.eagle.common.HeroType
import kotlin.math.min

@Suppress("unused")
class Hero173 : HeroType() { // 李元芳
    override fun tempSpeed(level: Int) = 340 + (min(level, 11) + 1) / 2 * 60
    override val tempSpeedName get() = Ability[abilities[0]]!!.name
}