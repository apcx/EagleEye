package apc.eagle.common

class Rune {

    var id = 0
    var name = ""
    var level = 1
    var color = ""
    val attributes = mutableMapOf<String, Int>()

    override fun toString() = json

    companion object {
        val idMap = mutableMapOf<Int, Rune>()
        val nameMap = mutableMapOf<String, Rune>()
    }
}