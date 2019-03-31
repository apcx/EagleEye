package apc.eagle.common

import kotlin.math.max

class Hero(val type: HeroType) {

    var level = 1
    var baseAttackSpeed = 0
    var auraSpeed = 0
    var attackIndex = 0

    fun updateAttributes() {
        baseAttackSpeed = type.bonusAttackSpeed * (level - 1)
        auraSpeed = 0
        type.equips.map(Int::toEquip).filterNotNull().forEach {
            baseAttackSpeed += it.attackSpeed
            when (it.name) {
                "极影" -> auraSpeed = 300
                "凤鸣指环" -> auraSpeed = max(auraSpeed, 200)
            }
        }

        val rune = type.runeConfig.toOneRune()
        baseAttackSpeed += rune.attackSpeed
    }

    val expectedSpeed get() = baseAttackSpeed + auraSpeed + type.passiveSpeed
}