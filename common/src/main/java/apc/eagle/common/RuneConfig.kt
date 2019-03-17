package apc.eagle.common

import java.util.*

class RuneConfig {

    var id = 0
    var name = ""
    var hero = 0
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
            attackSpeed += it.first.attackSpeed * it.second
        }
    }

    fun copyTo(target: RuneConfig) {
        target.ids.forEachIndexed { index, it ->
            it.clear()
            it.putAll(ids[index])
        }
    }

    companion object : TreeMap<Int, RuneConfig>()
}