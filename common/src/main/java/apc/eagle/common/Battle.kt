package apc.eagle.common

class Battle() {

    private val forces = mutableListOf<List<Hero>>()
    private val regenEvents = mutableListOf<Event>()
    val events = mutableListOf<Event>()
    val logs = mutableListOf<Event>()
    var time = 0

    constructor(blue: Hero, red: Hero) : this() {
        blue.battle = this
        red.battle = this
        blue.active = true
        red.force = 1
        forces += listOf(blue)
        forces += listOf(red)
    }

    fun fight(): List<Event> {
        regenEvents.clear()
        events.clear()
        logs.clear()
        time = 0
        val heroes = forces.flatten()
        heroes.forEach(Hero::reset)
        while (time < 90_000 && forces.sumBy { force -> if (force.any { it.hp > 0 }) 1 else 0 } >= 2) {
            if (regenEvents.isEmpty() && heroes.any { it.mhp - it.hp >= it.regen }) {
                val firstRegen = time - GameData.MS_FRAME + 2
                regenEvents += heroes.map { Event(firstRegen, it, Regen, 5000) }
            }

            regenEvents.filter(::ready).forEach { it.onTick() }
            events.filter(::ready).sortedBy { it.time }
                .forEach { if (it.onTick() || it.target.hp <= 0) events -= it }

            val liveHeroes = heroes.filter { it.hp > 0 }
            liveHeroes.filter { it.active }.forEach { hero ->
                if (hero.hp > 0) {
                    liveHeroes.filter { it.force != hero.force && it.hp > 0 }.sortedBy { it.hp }.firstOrNull()
                        ?.let(hero::onAction)
                }
            }
            time += GameData.MS_FRAME
        }
        return logs
    }

    private fun ready(event: Event) = event.time <= time
}