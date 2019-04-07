package apc.eagle.common

import java.util.*
import kotlin.math.max

open class Ability() {

    var id = 0
    var name = ""
    var slot = 0
    var cd = 1000

    var swing = 2
    var speedModel = 0
    lateinit var speeds: IntArray

    var attackFactor = 0
    var extraFactor = 0
    var magicFactor = 0
    var levelFactor = 0
    var targetHpFactor = 0
    var bonus = 0
    var type = 0
    var hasExpertise = false
    var hasOrb = false
    var canCritical = false
    var omegaCritical = false
    var isSpell = false
    var duration = 0
    var tipOn = ""
    var tipOff = ""

    constructor(name: String, cd: Int, swing: Int) : this() {
        this.name = name
        this.cd = cd
        this.swing = swing
        attackFactor = 100
        type = TYPE_PHYSICAL
        hasExpertise = true
        hasOrb = true
        canCritical = true
    }

    constructor(name: String, type: Int) : this() {
        this.name = name
        this.type = type
    }

    fun initSpeeds() {
        if (!::speeds.isInitialized) speeds = buildSpeeds()
    }

    private fun buildSpeeds(max: Int = 200): IntArray {
        val list = mutableListOf<Int>()
        var old = rawFrames(0)
        repeat(max * 10 - 1) {
            val speed = 1 + it
            val frames = rawFrames(speed)
            if (old != frames) {
                old = frames
                list += speed
            }
        }
        return list.toIntArray()
    }

    private fun rawFrames(speed: Int): Int {
        val ms = cd * 1000 / (1000 + speed)
        var frames = ms / GameData.MS_FRAME
        if (ms % GameData.MS_FRAME != 0) ++frames
        return frames
    }

    internal fun attackFrames(speed: Int) = SpeedModel[speedModel]?.frames(speed) ?: rawFrames(speed) + swing

    fun damage(attacker: Hero, target: Hero): Int {
        var damage = attacker.attack * attackFactor / 100
        if (hasExpertise) damage += attacker.expertise
        var factor = defenseFactor(attacker, target)
        if (attacker.hasExecute && target.hp < target.mhp * 50 / 100) factor *= 1.3f
        if (canCritical && !omegaCritical) damage = attacker.criticalDamage(damage, factor)
        damage += attacker.extraAttack * extraFactor / 100
        damage += attacker.magic * magicFactor / 100
        damage += attacker.level * levelFactor
        damage += target.hp * targetHpFactor / 100
        damage += bonus
        if (canCritical && omegaCritical) damage = attacker.criticalDamage(damage, factor)
        return (damage * factor).toInt()
    }

    private fun defenseFactor(attacker: Hero, target: Hero) = when (type) {
        TYPE_PHYSICAL -> {
            val defense = max(0, target.defense - attacker.penetrate)
            600f / (600 + defense * (100 - attacker.penetrateRate) / 100)
        }
        TYPE_MAGIC -> {
            val defense = max(0, target.magicDefense - attacker.magicPenetrate)
            600f / (600 + defense * (100 - attacker.magicPenetrateRate) / 100)
        }
        else -> 1f
    }

    override fun toString() = toJson()

    companion object : HashMap<Int, Ability>() {
        const val TYPE_REGEN = 0
        const val TYPE_PHYSICAL = 1
        const val TYPE_MAGIC = 2
        const val TYPE_REAL = 3
        const val TYPE_BUFF = 4
        @Suppress("SpellCheckingInspection")
        const val TYPE_DEBUFF = 5
    }
}

object Corrupt : Ability("破败", TYPE_PHYSICAL) {
    init {
        targetHpFactor = 8
    }
}

object Lightning : Ability("电弧", TYPE_MAGIC) {
    init {
        attackFactor = 30
        bonus = 100
        canCritical = true
        omegaCritical = true
    }
}

object EnchantRune : Ability("符文大剑", TYPE_MAGIC) {
    init {
        magicFactor = 50
        bonus = 50
    }
}

object EnchantVoodoo : Ability("巫术法杖", TYPE_MAGIC) {
    init {
        attackFactor = 30
        magicFactor = 80
    }
}

object EnchantMaster : Ability("宗师之力", TYPE_PHYSICAL) {
    init {
        attackFactor = 100
    }
}

object EnchantGauntlets : Ability("冰痕之握", TYPE_PHYSICAL) {
    init {
        levelFactor = 20
        bonus = 150
    }
}

object Enchant : Ability("光辉之剑", TYPE_MAGIC) {
    init {
        attackFactor = 50
        magicFactor = 30
    }
}

object Regen : Ability("每5秒回血", TYPE_REGEN)

object Storm : Ability("暴风", TYPE_BUFF) {
    init {
        duration = 2_000
        tipOn = "攻速+30%"
        tipOff = "攻速-30%"
    }
}

object Iron30 : Ability("寒铁", TYPE_DEBUFF) {
    init {
        duration = 3_000
        tipOn = "攻速-30%"
        tipOff = "攻速+30%"
    }
}

object Iron15 : Ability("寒铁", TYPE_DEBUFF) {
    init {
        duration = 3_000
        tipOn = "攻速-15%"
        tipOff = "攻速+15%"
    }
}