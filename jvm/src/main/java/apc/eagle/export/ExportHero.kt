package apc.eagle.export

import apc.eagle.common.HeroType
import apc.eagle.common.toBean
import apc.eagle.common.toJson

fun HeroType.export() = toJson().toBean<ExportHero>()?.apply {
    cat = when (category) {
        "坦克" -> 1
        "战士" -> 2
        "刺客" -> 3
        "法师" -> 4
        "射手" -> 5
        "辅助" -> 6
        else -> 0
    }
    cat2 = when (secondaryCategory) {
        "坦克" -> 1
        "战士" -> 2
        "刺客" -> 3
        "法师" -> 4
        "射手" -> 5
        "辅助" -> 6
        else -> 0
    }
}

class ExportHero : HeroType() {
    var cat = 0
    var cat2 = 0
}