package apc.eagle.common

import kotlin.math.max
import kotlin.math.min

class Event(var time: Int, val target: Hero, var type: Int) : Cloneable {

    var ticks = 1
    var period = 0
    lateinit var attacker: Hero
    lateinit var ability: Ability

    var damage = 0
    var critical = false
    var hp = 0

    constructor(time: Int, target: Hero, attacker: Hero, ability: Ability = attacker.type.attackAbilities[0])
            : this(time, target, TYPE_HIT) {
        this.attacker = attacker
        this.ability = ability
    }

    constructor(time: Int, target: Hero, ability: Ability, period: Int, ticks: Int = 0) : this(
        time,
        target,
        TYPE_REGEN
    ) {
        this.ability = ability
        this.ticks = ticks
        this.period = period
    }

    constructor(target: Hero, ability: Ability) : this(target.battle.time, target, TYPE_ON) {
        this.ability = ability
    }

    public override fun clone() = (super.clone() as Event).also {
        if (type == TYPE_ON) {
            it.type = TYPE_OFF
            it.time = target.battle.time + ability.duration
        } else {
            it.time = target.battle.time
        }
    }

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
                    var damage = ability.damage(attacker, target)
                    hitLog.damage = damage
                    when (target.ironSpeed) {
                        300 -> if (attacker.addBuff(Iron30)) attacker.tempSpeed -= target.ironSpeed
                        150 -> if (attacker.addBuff(Iron15)) attacker.tempSpeed -= target.ironSpeed
                    }
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
                    target.hp = max(0, target.hp - damage)
                    target.battle.logs.subList(target.battle.logs.indexOf(hitLog), target.battle.logs.size)
                        .forEach { it.hp = target.hp }
                    if (ability.hasOrb) {
                        if (attacker.hasCorrupt)
                            target.battle.events += Event(now + GameData.MS_FRAME, target, attacker, Corrupt)
                        if (attacker.hasLightning && now >= attacker.nextLightningTime) {
                            attacker.lightningRage += 30
                            if (attacker.lightningRage >= 100) {
                                attacker.lightningRage -= 100
                                attacker.nextLightningTime = now + 500
                                target.battle.events += Event(now + GameData.MS_FRAME * 2, target, attacker, Lightning)
                            }
                        }
                    }
//                    if (ability.isSpell) { // todo: move to casting moment
//                        val enchant = attacker.enchant
//                        if (enchant != null && now >= attacker.nextEnchantTime) {
//                            attacker.nextEnchantTime += now + 2_000
//                            target.battle.events += Event(now + GameData.MS_FRAME, target, attacker, enchant)
//                        }
//                    }
                }
                TYPE_OFF -> {
                    when (ability) {
                        Storm -> {
                            target.tempSpeed -= 300
                            target.battle.logs += this
                        }
                        Iron30 -> {
                            target.tempSpeed += 300
                            target.battle.logs += this
                        }
                        Iron15 -> {
                            target.tempSpeed += 150
                            target.battle.logs += this
                        }
                    }
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