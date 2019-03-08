package apc.eagle.common

class Equip {

    var id = 0
    var name = ""
    var category = 0
    var price = 0
    var top = 0
    var hp = 0
    var regen = 0
    var attack = 0
    var attackSpeed = 0
    var critical = 0
    var magic = 0
    var cdr = 0
    var defense = 0
    var magicDefense = 0
    var moveSpeed = 0

    override fun toString() = toJson()

    companion object {
        val idMap = mutableMapOf<Int, Equip>()
        val nameMap = mutableMapOf<String, Equip>()
    }
}