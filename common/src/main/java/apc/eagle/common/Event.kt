package apc.eagle.common

import apc.eagle.common.hero.BurnSoul
import apc.eagle.common.hero.RetributionShot
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
        time: Int, target: Hero, attacker: Hero, ability: Ability = attacker.type.attackAbilities[0],
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

    fun onTick(now: Int): Boolean {
        if (target.hp > 0) {
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
                    if (ability.type == Ability.TYPE_MAGIC && target.magicShield > 0) {
                        val shieldDamage = min(damage, target.magicShield)
                        target.magicShield -= shieldDamage
                        if (target.magicShield <= 0) {
                            val log = Event(target.battle.time, target, TYPE_OFF)
                            log.ability = Ability("迷雾", Ability.TYPE_BUFF)
                            log.ability.tipOff = "护盾击破"
                            target.battle.logs += log
                        }
                        damage -= shieldDamage
                    }
                    when (target.ironSpeed) {
                        300 -> attacker.addBuff(Iron30)
                        150 -> attacker.addBuff(Iron15)
                    }
                    when (attacker.type.name) {
                        "后羿" -> if (ability.canExpertise) attacker.addBuff(RetributionShot)
                        "黄忠" -> if (ability.canExpertise) attacker.addBuff(BurnSoul)
                    }
                    target.hp = max(0, target.hp - damage)
                    hitLog.hp = target.hp
                    if (ability.canOrb) {
                        if (attacker.hasCorrupt) {
                            val event = target.buff(Corrupt)
                            if (event == null)
                                target.battle.events += Event(now + GameData.MS_FRAME, target, attacker, Corrupt)
                            else
                                event.time = now + GameData.MS_FRAME
                        }
                        if (attacker.enchanting) {
                            attacker.enchanting = false
                            target.battle.events += Event(now + GameData.MS_FRAME, target, attacker, attacker.enchant!!)
                        }
                        if (attacker.hasLightning && now >= attacker.nextLightningTime) {
                            attacker.lightningRage += 30
                            if (attacker.lightningRage >= 100) {
                                attacker.lightningRage -= 100
                                attacker.nextLightningTime = now + 500
                                target.battle.events += Event(now + GameData.MS_FRAME * 2, target, attacker, Lightning)
                            }
                        }
                    }
                }
                TYPE_OFF -> {
                    ability.off(target, stacks)
                    target.battle.logs += this
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