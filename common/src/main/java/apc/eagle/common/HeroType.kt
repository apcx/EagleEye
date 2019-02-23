package apc.eagle.common

import apc.eagle.common.GameData.MS_FRAME
import com.google.gson.Gson

class HeroType : UnitType() {

    var category = ""
    var secondaryCategory = ""
    var order = 0
    var bonusHp = 0
    var bonusRegen = 0
    var bonusAttack = 0
    var bonusAttackSpeed = 0
    var bonusDefense = 0
    val defaultEquips = arrayOf(IntArray(6), IntArray(6), IntArray(6))
    val equips = IntArray(6)
    val defaultRunes = IntArray(3)
    val abilities = IntArray(4)

    var speedModel = 0
    var swing = 2
    lateinit var speeds: IntArray

    fun useDefaultEquips(index: Int = 0) {
        defaultEquips[index].copyInto(equips)
    }

    fun setEquips(vararg names: String) {
        equips.fill(0)
        names.mapNotNull(Equip.nameMap::get).map { it.id }.toIntArray().copyInto(equips)
    }

    fun rawFrames(speed: Int): Int {
        val ms = attackCd * 1000 / (1000 + speed)
        var frames = ms / MS_FRAME
        if (ms % MS_FRAME != 0) ++frames
        return frames
    }

    fun buildSpeeds() {
        if (!::speeds.isInitialized) {
            val list = mutableListOf<Int>()
            var old = rawFrames(0)
            repeat(1999) {
                val speed = 1 + it
                val frames = rawFrames(speed)
                if (old != frames) {
                    old = frames
                    list += speed
                }
            }
            speeds = list.toIntArray()
        }
    }

    override fun toString() = json

    companion object {
        val idMap = mutableMapOf<Int, HeroType>()
        val nameMap = mutableMapOf<String, HeroType>()
    }
}

@Suppress("SpellCheckingInspection")
private val gson = Gson()
val Any?.json get() = gson.toJson(this)!!