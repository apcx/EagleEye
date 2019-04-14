package apc.eagle.common

fun Int.toEquip() = Equip.idMap[this]
fun String.toEquip() = Equip.nameMap[this]

class Equip {

    var id = 0
    var name = ""
    var category = 0
    var price = 0
    var top = 0
    var level = 0
    var hp = 0
    var regen = 0
    var attack = 0
    var haste = 0
    var critical = 0
    var magic = 0
    var cdr = 0
    var defense = 0
    var magicDefense = 0
    var moveSpeed = 0

    override fun toString() = toJson()

    companion object {
        const val CATEGORY_ATTACK = 1
        const val CATEGORY_MAGIC = 2
        const val CATEGORY_DEFENSE = 3
        const val CATEGORY_MOVE = 4
        const val CATEGORY_MOB = 5
        const val CATEGORY_SUPPORT = 7
        val idMap = mutableMapOf<Int, Equip>()
        val nameMap = mutableMapOf<String, Equip>()
    }
}