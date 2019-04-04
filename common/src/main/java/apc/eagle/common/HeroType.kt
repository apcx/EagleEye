package apc.eagle.common

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
    val equipConfigs = Array<EquipConfig?>(6) { null }
    val equips = IntArray(6)
    var defaultRuneConfig = 0
    val recommendedRuneConfigs = mutableListOf<RuneConfig>()
    val runeConfig = RuneConfig()
    val attackAbilities = mutableListOf<Ability>()
    val abilities = IntArray(4)
    open fun passiveSpeed(level: Int) = 0
    open val passiveSpeedName get() = attackAbilities[0].name
    open fun tempSpeed(level: Int) = 0
    open val tempSpeedName = ""
    open val canCritical = true

    fun applyEquipConfig(index: Int = 0) {
        equipConfigs[index]?.equips?.copyInto(equips)
    }

    fun resetRunes() {
        RuneConfig[defaultRuneConfig]?.copyTo(runeConfig)
    }

    open fun initSpeeds() {
        attackAbilities.forEach(Ability::initSpeeds)
    }

    open fun attackFrames(speed: Int, index: Int = 0) = attackAbilities[index].attackFrames(speed)

    override fun toString() = toJson()

    companion object {
        val idMap = mutableMapOf<Int, HeroType>()
        val nameMap = mutableMapOf<String, HeroType>()
    }
}

@Suppress("SpellCheckingInspection")
private val gson = Gson()

fun Any?.toJson() = gson.toJson(this)!!