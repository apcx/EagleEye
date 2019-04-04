package apc.eagle.common.hero

import apc.eagle.common.Ability
import apc.eagle.common.HeroType
import kotlin.math.min

@Suppress("unused")
class Hero174 : HeroType() { // 虞姬
    override fun passiveSpeed(level: Int) = 180 + (min(level, 11) + 1) / 2 * 20
    override val passiveSpeedName get() = Ability[abilities[1]]!!.name
    override fun tempSpeed(level: Int) = if (level >= 4) 200 + level / 4 * 100 else 0
    override val tempSpeedName get() = Ability[abilities[2]]!!.name
}