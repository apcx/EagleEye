package apc.eagle.common

class HasteModel(val swing: Int = 2) {

    lateinit var heroes: Array<String>
    lateinit var speeds: IntArray

    fun frames(speed: Int) = 15 - speeds.indexOfLast { speed >= it } + swing

    companion object : HashMap<Int, HasteModel>() {
        init {
            put(2, HasteModel(1).apply {
                heroes = arrayOf("刘备", "兰陵王", "娜可露露", "扁鹊", "不知火舞", "艾琳", "成吉思汗", "黄忠", "孙尚香", "孙膑")
                speeds = intArrayOf(16, 92, 168, 274, 380, 532, 698, 910, 1168, 1532)
            })
            put(3, HasteModel(1).apply {
                heroes = arrayOf("苏烈", "孙策", "猪八戒", "达摩", "李信", "宫本武藏", "百里玄策", "裴擒虎", "米莱狄", "上官婉儿", "伽罗")
            })
            put(4, HasteModel().apply {
                heroes = arrayOf("司马懿", "李元芳")
                speeds = intArrayOf(10, 82, 166, 262, 376, 514, 682, 864, 1092, 1394, 1788)
            })
            put(10, HasteModel(1).apply {
                heroes = arrayOf("后羿")
                speeds = intArrayOf(10, 82, 165, 262, 300, 426, 576, 774, 1032, 1364, 1834)
            })
        }

        fun initHeroes() {
            forEach { id, model ->
                model.heroes.mapNotNull(String::toHero).forEach {
                    it.attackAbilities[0].swing = model.swing
                    if (model::speeds.isInitialized) {
                        it.attackAbilities[0].hasteModel = id
                        it.attackAbilities[0].speeds = model.speeds
                    }
                }
            }
        }
    }
}