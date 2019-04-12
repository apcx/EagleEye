package apc.eagle.common

import kotlin.math.min

class Hero(val type: HeroType) {

    var level = 15
    private val equips get() = type.equips.map(Int::toEquip).filterNotNull()
    val abilityLevels = IntArray(4)
    var baseAttackSpeed = 0
    var auraSpeed = 0
    val expectedSpeed get() = baseAttackSpeed + auraSpeed + type.passiveSpeed
    val attackSpeed get() = baseAttackSpeed + auraSpeed

    val criticalDamageRuneBonus get() = (critical + 7) * (criticalDamage + 36) - (critical * criticalDamage + 7 * 1000)
    val criticalRuneBonus get() = 16 * (criticalDamage - 1000)

    private fun critical(attack: Int) = attack * (critical * criticalDamage + (1000 - critical) * 1000) / 1000_000
    var avgAttack = IntArray(2)
    fun avgAttack(ability: Ability = type.attackAbilities[0]) =
        critical(attack * ability.attackFactor / 100 + expertise) + ability.extraDamage

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
    var cdr = 0
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
    var attackIndex = 0
    var nextAttackTime = 0
    private var criticalRage = 0f
    var nextCastTime = IntArray(4)
    var nextLightningTime = 0
    var lightningRage = 0
    var nextEnchantTime = 0
    var enchanting = false
    var heroRage = 0

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
        critical = type.baseCritical
        criticalDamage = 1000 + type.baseCriticalDamage
        magic = 0
        magicDefense = 50 + type.bonusMagicDefense * bonusLevel / 10000
        magicPenetrate = 0
        magicPenetrateRate = 0
        cdr = 0
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
            cdr += it.cdr
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
        cdr += rune.cdr

        when (type.skins.last().type) {
            Skin.TYPE_ATTACK -> extraAttack += 10
            Skin.TYPE_MAGIC -> magic += 10
            Skin.TYPE_HP -> mhp += 120
        }
        if (equips.has("破魔刀")) magicDefense += min(attack * 40 / 100, 300)
        if (cdr > 400) cdr = 400

        if (type.learn.isNotEmpty()) {
            abilityLevels.fill(0)
            for (level in 1..level) ++abilityLevels[type.learn[level - 1] - 1]
        }
        type.updateSpecificAttributes(this)
    }

    private fun Iterable<Equip>.has(name: String) = any { it.name == name }

    fun reset() {
        updateAttributes()
        hp = mhp
        nextAttackTime = 0
        criticalRage = 0f
        nextCastTime.fill(0)
        nextLightningTime = 0
        lightningRage = 0
        nextEnchantTime = 0
        enchanting = false
        magicShield = if (equips.has("魔女斗篷")) 200 + level * 120 else 0
        heroRage = 0
    }

    fun criticalDamage(damage: Int, factor: Float): Int {
        criticalRage += damage * min(critical, 1000) * factor
        val cost = damage * 1000 * factor
        return if (criticalRage >= cost) {
            criticalRage -= cost
            battle.logs.last().critical = true
            if (hasStorm) addBuff(Storm)
            damage * criticalDamage / 1000
        } else {
            damage
        }
    }

    fun buff(ability: Ability) = battle.events.find { it.target == this && it.ability == ability }

    fun addBuff(ability: Ability) {
        var buff = buff(ability)
        val add = if (buff == null) {
            buff = Event(this, ability)

            val event = buff.clone()
            event.type = Event.TYPE_OFF
            event.time = battle.time + ability.duration
            battle.events += event
            true
        } else {
            buff.time = battle.time + ability.duration
            if (buff.stacks < ability.maxStacks) {
                ++buff.stacks

                buff = buff.clone()
                buff.type = Event.TYPE_ON
                true
            } else {
                false
            }
        }
        if (add) {
            ability.on(this)
            battle.logs += buff
        }
    }

    fun onAction(target: Hero) {
        type.onAction(this, target)
    }

    fun attackOnTime() = battle.time >= nextAttackTime
    fun abilityOnTime(index: Int) = battle.time >= nextCastTime[index] && abilityLevels[index] > 0
}