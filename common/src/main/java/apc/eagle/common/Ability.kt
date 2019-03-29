package apc.eagle.common

import java.util.*

class Ability {

    var id = 0
    var name = "攻击"
    var slot = 0
    var cd = 1000

    @Transient
    var swing = 2

    @Transient
    var speedModel = 0

    @Transient
    lateinit var speeds: IntArray

    fun initSpeeds() {
        if (!::speeds.isInitialized) speeds = buildSpeeds()
    }

    private fun buildSpeeds(max: Int = 200): IntArray {
        val list = mutableListOf<Int>()
        var old = rawFrames(0)
        repeat(max * 10 - 1) {
            val speed = 1 + it
            val frames = rawFrames(speed)
            if (old != frames) {
                old = frames
                list += speed
            }
        }
        return list.toIntArray()
    }

    private fun rawFrames(speed: Int): Int {
        val ms = cd * 1000 / (1000 + speed)
        var frames = ms / GameData.MS_FRAME
        if (ms % GameData.MS_FRAME != 0) ++frames
        return frames
    }

    internal fun attackFrames(speed: Int) = SpeedModel[speedModel]?.frames(speed) ?: rawFrames(speed) + swing

    override fun toString() = toJson()

    companion object : HashMap<Int, Ability>()
}