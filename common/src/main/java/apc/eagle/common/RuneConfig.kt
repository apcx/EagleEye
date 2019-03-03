package apc.eagle.common

import java.util.*

class RuneConfig {

    var id = 0
    var name = ""
    var hero = 0
    var red = 0
    var blue = 0
    var green = 0

    override fun equals(other: Any?) = other is RuneConfig
            && other.red == red
            && other.blue == blue
            && other.green == green

    override fun hashCode(): Int {
        var result = red
        result = 31 * result + blue
        result = 31 * result + green
        return result
    }

    companion object : TreeMap<Int, RuneConfig>()
}