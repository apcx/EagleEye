package apc.eagle.common

import apc.eagle.common.hero.BurnSoul
import apc.eagle.common.hero.RetributionShot
import apc.eagle.common.hero.Wound
import kotlin.math.max
import kotlin.math.min

class Event(var time: Int, val target: Hero, var type: Int) : Cloneable {

    private var ticks = 1
    private var period = 0
    var stacks = 1
    lateinit var attacker: Hero
    lateinit var ability: Ability
    var abilityFactor = 100

    var damage = 0
    var critical = false
    var hp = -1

    constructor(
        time: Int, target: Hero, attacker: Hero, ability: Ability = attacker.type.attackAbilities[attacker.attackIndex],
        abilityFactor: Int = 100
    ) : this(time, target, TYPE_HIT) {
        this.attacker = attacker
        this.ability = ability
        this.abilityFactor = abilityFactor
    }

    constructor(time: Int, target: Hero, ability: Ability, period: Int, ticks: Int = 0)
            : this(time, target, TYPE_REGEN) {
        this.ability = ability
        this.ticks = ticks
        this.period = period
    }

    constructor(target: Hero, ability: Ability) : this(target.battle.time, target, TYPE_ON) {
        this.ability = ability
    }

    public override fun clone() = (super.clone() as Event).also { it.time = target.battle.time }

    fun onTick(): Boolean {
        if (target.hp > 0) {
            val now = target.battle.time
            when (type) {
                TYPE_REGEN -> {
                    val hurt = target.mhp - target.hp
                    if (hurt > 0) {
                        val regen = min(target.regen, hurt)
                        target.hp += regen
                        val log = clone()
                        log.damage = regen
                        log.hp = target.hp
                        target.battle.logs += log
                    }
                }
                TYPE_HIT -> {
                    val hitLog = clone()
                    target.battle.logs += hitLog
                    var damage = ability.damage(attacker, target, abilityFactor)
                    hitLog.damage = damage
                    if (attacker.type.name == "伽罗" && (ability.canExpertise || ability == Wound)) {
                        val descriptionDamage = ability.descriptionDamage(attacker)
                        target.onHitShield(Hero::magicShield, descriptionDamage, "魔女斗篷")
                        target.onHitShield(Hero::standShield, descriptionDamage, "血魔之怒")
                    }
                    if (ability.type == Ability.TYPE_MAGIC) damage = target.onHitShield(Hero::magicShield, damage, "魔女斗篷")
                    if (ability.type != Ability.TYPE_REAL) damage =
                        target.onHitShield(Hero::standShield, damage, "血魔之怒")
                    when (target.ironSpeed) {
                        300 -> attacker.addBuff(Iron30)
                        150 -> attacker.addBuff(Iron15)
                    }
                    val events = target.battle.events
                    when (attacker.type.name) {
                        "后羿" -> if (ability.canExpertise) attacker.addBuff(RetributionShot)
                        "黄忠" -> if (ability.canExpertise) attacker.addBuff(BurnSoul)
                        "伽罗" -> if (ability == attacker.type.attackAbilities[1]) {
                            events.filter { it.target == target && it.ability == Wound }.forEach { it.time = now + 500 }
                            events += Event(now + 500, target, attacker, Wound)
                            events += Event(now + 1000, target, attacker, Wound)
                            events += Event(now + 1500, target, attacker, Wound)
                        }
                    }
                    target.hp = max(0, target.hp - damage)
                    hitLog.hp = target.hp
                    if (target.hasLastStand && now >= target.nextStandTime && target.hp < target.mhp * 30 / 100) {
                        target.nextStandTime = now + 90_000
                        target.addBuff(LastStand)
                    }
                    if (ability.canOrb) {
                        if (attacker.hasCorrupt) {
                            val event = target.buff(Corrupt)
                            if (event == null)
                                events += Event(now + GameData.MS_FRAME, target, attacker, Corrupt)
                            else
                                event.time = now + GameData.MS_FRAME
                        }
                        if (attacker.enchanting) {
                            attacker.enchanting = false
                            events += Event(now + GameData.MS_FRAME, target, attacker, attacker.enchant!!)
                        }
                        if (attacker.hasLightning && now >= attacker.nextLightningTime) {
                            attacker.lightningRage += 30
                            if (attacker.lightningRage >= 100) {
                                attacker.lightningRage -= 100
                                attacker.nextLightningTime = now + 500
                                events += Event(now + GameData.MS_FRAME * 2, target, attacker, Lightning)
                            }
                        }
                    }
                }
                TYPE_OFF -> {
                    ability.off(target, stacks)
                    if (!ability.channel) target.battle.logs += this
                }
            }
        }
        time += period
        if (ticks > 0) {
            --ticks
            if (ticks <= 0) return true
        }
        return false
    }

    companion object {
        const val TYPE_REGEN = 0
        const val TYPE_HIT = 1
        const val TYPE_ON = 2
        const val TYPE_OFF = 3
    }
}