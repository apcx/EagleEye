package apc.eagle.common

class Battle() {

    val heroes = Array(2) { mutableListOf<Hero>() }

    constructor(blue: HeroType, red: HeroType) : this() {
        heroes[0].add(Hero(blue))
        heroes[1].add(Hero(red))
    }

    fun fight() {

    }
}