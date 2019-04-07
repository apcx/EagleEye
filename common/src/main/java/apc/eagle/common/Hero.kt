package apc.eagle.common

import kotlin.math.min

class Hero(val type: HeroType) {

    val equips get() = type.equips.map(Int::toEquip).filterNotNull()
    var level = 15
    var baseAttackSpeed = 0
    var auraSpeed = 0
    val expectedSpeed get() = baseAttackSpeed + auraSpeed + type.passiveSpeed(level)
    val attackSpeed get() = baseAttackSpeed + auraSpeed + type.passiveSpeed(level) + tempSpeed

    var mhp = 0
    var regen = 50
    var ironSpeed = 0
    private val baseAttack get() = type.baseAttack + type.bonusAttack * (level - 1) / 10000
    var extraAttack = 0
    val attack get() = baseAttack + extraAttack
    var expertise = 0
    var defense = 0
    var penetrate = 0
    var penetrateRate = 0
    var critical = 0
    var criticalDamage = 0
    var magic = 0
    var magicDefense = 0
    var magicPenetrate = 0
    var magicPenetrateRate = 0
    var hasCorrupt = false
    var hasLightning = false
    var hasStorm = false
    var hasExecute = false
    var enchant: Ability? = null

    lateinit var battle: Battle
    var force = 0
    var active = false
    var hp = 0
    var magicShield = 0
    var tempSpeed = 0
    var attackIndex = 0
    var nextAttackTime = 0
    private var criticalRage = 0f
    var nextCastTime = IntArray(4)
    var nextLightningTime = 0
    var lightningRage = 0
    var nextEnchantTime = 0

    init {
        updateAttributes()
    }

    fun updateAttributes() = apply {
        val bonusLevel = level - 1
        mhp = type.baseHp + type.bonusHp * bonusLevel / 10000
        regen = type.baseRegen + type.bonusRegen * bonusLevel / 10000
        baseAttackSpeed = type.bonusAttackSpeed * bonusLevel
        auraSpeed = 0
        ironSpeed = 0
        extraAttack = 0
        expertise = 0
        defense = type.baseDefense + type.bonusDefense * bonusLevel / 10000
        penetrate = 0
        penetrateRate = 0
        critical = 0
        criticalDamage = 2000
        magic = 0
        magicDefense = 50 + type.bonusMagicDefense * bonusLevel / 10000
        magicPenetrate = 0
        magicPenetrateRate = 0
        enchant = null

        val equips = equips
        equips.forEach {
            mhp += it.hp
            regen += it.regen
            baseAttackSpeed += it.attackSpeed
            extraAttack += it.attack
            defense += it.defense
            critical += it.critical
            magic += it.magic
            magicDefense += it.magicDefense
        }
        hasCorrupt = equips.has("末世")
        hasLightning = equips.has("闪电匕首")
        hasStorm = equips.has("影刃")
        hasExecute = equips.has("破军")
        if (equips.has("极影")) auraSpeed += 300
        else if (equips.has("凤鸣指环")) auraSpeed += 200
        if (equips.has("不祥征兆")) ironSpeed += 300
        else if (equips.has("守护者之铠")) ironSpeed += 150
        if (equips.has("破晓")) expertise += 50
        if (equips.has("纯净苍穹") or equips.has("逐日之弓")) expertise += 35
        else if (equips.has("速击之枪")) expertise += 30
        if (type.attackType == UnitType.RANGE) expertise *= 2
        if (equips.has("暗影战斧")) penetrate += 50 + level * 10
        else if (equips.has("陨星")) penetrate += 60
        when {
            equips.has("碎星锤") -> penetrateRate += 40
            equips.has("破晓") -> penetrateRate += if (type.attackType == UnitType.RANGE) 40 else 20
            equips.has("穿云弓") -> penetrateRate += if (type.attackType == UnitType.RANGE) 20 else 10
        }
        if (equips.has("无尽战刃")) criticalDamage += 400
        when {
            equips.has("符文大剑") -> enchant = EnchantRune
            equips.has("巫术法杖") -> enchant = EnchantVoodoo
            equips.has("宗师之力") -> enchant = EnchantMaster
            equips.has("冰痕之握") -> enchant = EnchantGauntlets
            equips.has("光辉之剑") -> enchant = Enchant
        }
        if (equips.has("虚无法杖")) magicPenetrateRate += 45

        val rune = type.runeConfig.toOneRune()
        mhp += rune.hp / 100
        regen += rune.regen / 100
        baseAttackSpeed += rune.attackSpeed
        extraAttack += rune.attack / 100
        defense += rune.defense / 100
        penetrate += rune.penetrate / 100
        critical += rune.critical
        criticalDamage += rune.criticalDamage
        magic += rune.magic / 100
        magicDefense += rune.magicDefense / 100
        magicPenetrate += rune.magicPenetrate / 100

        when (type.skins.last().type) {
            Skin.TYPE_ATTACK -> extraAttack += 10
            Skin.TYPE_MAGIC -> magic += 10
            Skin.TYPE_HP -> mhp += 120
        }
        if (equips.has("破魔刀")) magicDefense += min(attack * 40 / 100, 300)
    }

    private fun Iterable<Equip>.has(name: String) = any { it.name == name }

    fun reset() {
        hp = mhp
        tempSpeed = 0
        nextAttackTime = 0
        criticalRage = 0f
        nextCastTime.fill(0)
        nextLightningTime = 0
        lightningRage = 0
        magicShield = if (equips.has("魔女斗篷")) 2000 else 0
    }

    fun criticalDamage(damage: Int, factor: Float): Int {
        criticalRage += damage * min(critical, 1000) * factor
        val cost = damage * 1000 * factor
        return if (criticalRage >= cost) {
            criticalRage -= cost
            battle.logs.last().critical = true
            if (hasStorm && addBuff(Storm)) tempSpeed += 300
            damage * criticalDamage / 1000
        } else {
            damage
        }
    }

    fun addBuff(ability: Ability): Boolean {
        val event = battle.events.find { it.target == this && it.ability == ability }
        return if (event == null) {
            val log = Event(this, ability)
            battle.logs += log
            battle.events += log.clone()
            true
        } else {
            event.time = battle.time + ability.duration
            false
        }
    }

    fun onAction(time: Int, target: Hero) {
        type.onAction(time, this, target)
    }
}