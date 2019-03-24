package apc.eagle.common

import kotlin.math.max
import kotlin.math.min

class Hero(val type: HeroType) {

    var level = 1
    var baseAttackSpeed = 0
    var auraSpeed = 0
    var attackIndex = 0

    fun updateAttributes() {
        baseAttackSpeed = type.bonusAttackSpeed * (level - 1)
        auraSpeed = 0
        type.equips.map(Equip.idMap::get).filterNotNull().forEach {
            baseAttackSpeed += it.attackSpeed
            when (it.name) {
                "极影" -> auraSpeed = 300
                "凤鸣指环" -> auraSpeed = max(auraSpeed, 200)
            }
        }

        val rune = type.runeConfig.toOneRune()
        baseAttackSpeed += rune.attackSpeed
    }

    val expectedSpeed get() = min(baseAttackSpeed + auraSpeed + type.passiveSpeed, 2000)
}