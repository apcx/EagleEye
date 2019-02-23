package apc.eagle.common

class Hero(val type: HeroType) {

    var level = 1

    val attackSpeed
        get(): Int {
            var speed = type.bonusAttackSpeed * (level - 1)
            type.equips.map(Equip.idMap::get).filterNotNull().forEach {
                speed += it.attackSpeed
            }
            return speed
        }

    fun attackFrames(speed: Int) = SpeedModel[type.speedModel]?.frames(speed) ?: type.rawFrames(speed) + type.swing
}