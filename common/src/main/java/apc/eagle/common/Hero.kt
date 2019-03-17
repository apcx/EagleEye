package apc.eagle.common

import kotlin.math.min

class Hero(val type: HeroType) {

    var level = 1
    var baseAttackSpeed = 0

    fun updateAttributes() {
        baseAttackSpeed = type.bonusAttackSpeed * (level - 1)
        type.equips.map(Equip.idMap::get).filterNotNull().forEach {
            baseAttackSpeed += it.attackSpeed
        }

        val rune = type.runeConfig.toOneRune()
        baseAttackSpeed += rune.attackSpeed
    }

    val expectedSpeed get() = min(baseAttackSpeed + type.passiveSpeed, 2000)
}