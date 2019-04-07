package apc.eagle.common

import java.util.*

class RuneConfig {

    var name = ""
    val ids = Array(3) { linkedMapOf<Int, Int>() }

    fun toRunes(color: Int) = mutableListOf<Pair<Rune, Int>>().apply {
        ids[color - 1].forEach { id, count ->
            val rune = id.toRune()
            if (rune != null && count > 0) add(rune to count)
        }
    }

    fun toString(color: Int) = toRunes(color).joinToString("\n") { "${it.first.name} x ${it.second}" }

    fun toOneRune() = Rune().apply {
        val runes = mutableListOf<Pair<Rune, Int>>()
        repeat(3) { runes += toRunes(1 + it) }
        runes.forEach {
            hp += it.first.hp * it.second
            regen += it.first.regen * it.second
            attackSpeed += it.first.attackSpeed * it.second
            attack += it.first.attack * it.second
            defense += it.first.defense * it.second
            penetrate += it.first.penetrate * it.second
            critical += it.first.critical * it.second
            criticalDamage += it.first.criticalDamage * it.second
            magic += it.first.magic * it.second
            magicDefense += it.first.magicDefense * it.second
            magicPenetrate += it.first.magicPenetrate * it.second
        }
    }

    fun copyTo(target: RuneConfig) {
        target.ids.forEachIndexed { index, it ->
            it.clear()
            it += ids[index]
        }
    }

    companion object : TreeMap<Int, RuneConfig>()
}