package apc.eagle.common

import java.util.*

class Ability {

    var id = 0
    var name = ""
    var slot = 0
    var cd = 0

    override fun toString() = toJson()

    companion object : HashMap<Int, Ability>()
}