package apc.eagle.common

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

fun Int.toHero() = HeroType.idMap[this]
fun String.toHero() = HeroType.nameMap[this]

open class HeroType : UnitType() {
    var category = ""
    var secondaryCategory = ""
    var order = 0
    var baseCritical = 0
    var baseCriticalDamage = 0
    var bonusHp = 0
    var bonusRegen = 0
    var bonusAttack = 0
    var bonusHaste = 0
    var bonusDefense = 0
    var bonusMagicDefense = 0
    val equipConfigs = Array<EquipConfig?>(6) { null }
    val equips = IntArray(6)
    var defaultRuneConfig = 0
    val recommendedRuneConfigs = mutableListOf<RuneConfig>()
    val runeConfig = RuneConfig()
    val skins = mutableListOf<Skin>()
    open val preferredIcon get() = if (skins.size >= 2) skins[1].icon else skins[0].icon
    val attackAbilities = mutableListOf<Ability>()
    val abilities = Array(4) { Ability() }
    open val learn get() = IntArray(0)
    open val passiveHaste get() = 0
    open val passiveHasteName get() = attackAbilities[0].name
    open fun tempHaste(level: Int) = 0
    open val tempHasteName get() = ""
    open val specialAttackName get() = ""

    fun applyEquipConfig(index: Int = 0) {
        equipConfigs[index]?.equips?.copyInto(equips)
    }

    fun resetRunes() {
        RuneConfig[defaultRuneConfig]?.copyTo(runeConfig)
    }

    open fun initAbilities() {
        attackAbilities.forEach(Ability::initSpeeds)
    }

    open fun updateSpecificAttributes(hero: Hero) {}

    open fun attackFrames(haste: Int, index: Int = 0) = attackAbilities[index].attackFrames(haste)

    open fun onAction(actor: Hero, target: Hero) {
        if (actor.attackOnTime()) doAttack(actor, target)
    }

    open fun doAttack(actor: Hero, target: Hero) {
        val time = target.battle.time
        actor.nextAttackTime = time + attackFrames(actor.haste) * GameData.MS_FRAME
        actor.battle.events += Event(time + GameData.MS_FRAME * if (attackType == RANGE) 5 else 1, target, actor)
    }

    open fun doCast(actor: Hero, target: Hero, index: Int) {
        val time = target.battle.time
        val ability = abilities[index]
        val cd = (ability.cd + ability.bonusCd * (actor.abilityLevels[index] - 1)) * (1000 - actor.cdr) / 1000
        actor.nextCastTime[index] = time + cd
        if (actor.enchant != null && time >= actor.nextEnchantTime && ability.isSpell) {
            actor.nextEnchantTime = time + 2000
            actor.enchanting = true
        }
        when (ability.type) {
            Ability.TYPE_BUFF -> actor.addBuff(ability)
            else -> actor.battle.events += Event(time + GameData.MS_FRAME * 5, target, actor, ability)
        }
    }

    override fun toString() = toJson()

    companion object {
        val idMap = mutableMapOf<Int, HeroType>()
        val nameMap = mutableMapOf<String, HeroType>()
    }
}

@Suppress("SpellCheckingInspection")
val gson = Gson()

fun Any?.toJson() = gson.toJson(this)!!

inline fun <reified T> String.toBean() = try {
    gson.fromJson(this, T::class.java)
} catch (e: JsonSyntaxException) {
    null
}