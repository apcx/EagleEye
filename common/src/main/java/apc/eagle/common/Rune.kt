package apc.eagle.common

class Rune {

    var id = 0
    var name = ""
    var level = 1
    var color = ""

    var hp = 0
    var regen = 0
    var attack = 0
    var attackSpeed = 0
    var critical = 0
    var criticalDamage = 0
    var magic = 0
    var cdr = 0
    var defense = 0
    var magicDefense = 0
    var penetrate = 0
    var magicPenetrate = 0
    var drain = 0
    var magicDrain = 0
    var moveSpeed = 0

    override fun toString() = json

    companion object {
        val idMap = mutableMapOf<Int, Rune>()
        val nameMap = mutableMapOf<String, Rune>()
    }
}