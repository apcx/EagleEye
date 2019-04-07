package apc.eagle.common

open class UnitType {

    var id = 0
    var name: String = ""
    var attackType = MELEE
    var baseHp = 0
    var baseRegen = 0
    var baseAttack = 0
    var baseDefense = 0
    var baseMove = 0

    companion object {
        const val MELEE = 1
        const val RANGE = 2
    }
}