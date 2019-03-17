package apc.eagle.common

import apc.eagle.common.GameData.MS_FRAME
import com.google.gson.Gson

open class HeroType : UnitType() {

    var category = ""
    var secondaryCategory = ""
    var order = 0
    var bonusHp = 0
    var bonusRegen = 0
    var bonusAttack = 0
    var bonusAttackSpeed = 0
    var bonusDefense = 0
    val defaultEquips = Array(3) { IntArray(6) }
    val equips = IntArray(6)
    var defaultRuneConfig = 0
    val recommendedRuneConfigs = mutableListOf<RuneConfig>()
    val runeConfig = RuneConfig()
    val abilities = IntArray(4)

    var speedModel = 0
    var swing = 2
    lateinit var speeds: IntArray
    var passiveSpeed = 0

    fun useDefaultEquips(index: Int = 0) {
        defaultEquips[index].copyInto(equips)
    }

    fun setEquips(vararg names: String) {
        equips.fill(0)
        names.mapNotNull(Equip.nameMap::get).map { it.id }.toIntArray().copyInto(equips)
    }

    fun resetRunes() {
        RuneConfig[defaultRuneConfig]?.copyTo(runeConfig)
    }

    fun initSpeeds() {
        if (!::speeds.isInitialized) speeds = buildSpeeds(attackCd)
    }

    open fun attackFrames(speed: Int) = SpeedModel[speedModel]?.frames(speed) ?: speedFrames(attackCd, speed) + swing

    override fun toString() = toJson()

    companion object {
        val idMap = mutableMapOf<Int, HeroType>()
        val nameMap = mutableMapOf<String, HeroType>()

        fun speedFrames(cd: Int, speed: Int): Int {
            val ms = cd * 1000 / (1000 + speed)
            var frames = ms / MS_FRAME
            if (ms % MS_FRAME != 0) ++frames
            return frames
        }

        fun buildSpeeds(cd: Int, max: Int = 200): IntArray {
            val list = mutableListOf<Int>()
            var old = speedFrames(cd, 0)
            repeat(max * 10 - 1) {
                val speed = 1 + it
                val frames = speedFrames(cd, speed)
                if (old != frames) {
                    old = frames
                    list += speed
                }
            }
            return list.toIntArray()
        }
    }
}

@Suppress("SpellCheckingInspection")
private val gson = Gson()

fun Any?.toJson() = gson.toJson(this)!!