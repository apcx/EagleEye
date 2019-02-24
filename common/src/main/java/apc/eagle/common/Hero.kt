package apc.eagle.common

class Hero(val type: HeroType) {

    var level = 1
    var baseAttackSpeed = 0

    fun updateAttributes() {
        baseAttackSpeed = type.bonusAttackSpeed * (level - 1)
        type.equips.map(Equip.idMap::get).filterNotNull().forEach {
            baseAttackSpeed += it.attackSpeed
        }
        type.runes.forEach { id, count ->
            val rune = Rune.idMap[id]!!
            baseAttackSpeed += rune.attackSpeed * count
        }
    }

    fun attackFrames(speed: Int) = SpeedModel[type.speedModel]?.frames(speed) ?: type.rawFrames(speed) + type.swing
}