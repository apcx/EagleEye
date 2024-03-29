package apc.eagle.tdr

import apc.eagle.common.HeroType
import apc.eagle.common.UnitType
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class HeroInfo : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 1)
    var name = ""

    @ExcelProperty(index = 57)
    var attackType = "普攻近战类型"

    @ExcelProperty("基础生命回复", index = 59)
    var baseRegen = 0

    @ExcelProperty("成长生命回复", index = 60)
    var bonusRegen = 0

    @ExcelProperty("成长攻击速度", index = 61)
    var bonusHaste = 0

    @ExcelProperty("基础生命", index = 62)
    var baseHp = 0

    @ExcelProperty("基础攻击力", index = 63)
    var baseAttack = 0

    @ExcelProperty("基础护甲", index = 65)
    var baseDefense = 0

    @ExcelProperty("基础移动速度", index = 68)
    var baseMove = 0

    @ExcelProperty("基础暴击率", index = 77)
    var baseCritical = 0

    @ExcelProperty("基础暴击效果", index = 78)
    var baseCriticalDamage = 0

    @ExcelProperty("生命成长率", index = 79)
    var bonusHp = 0

    @ExcelProperty("攻击力成长率", index = 80)
    var bonusAttack = 0

    @ExcelProperty("护甲成长率", index = 82)
    var bonusDefense = 0

    @ExcelProperty("抗性成长率", index = 83)
    var bonusMagicDefense = 0

    @ExcelProperty(index = 90)
    var category = ""

    @ExcelProperty(index = 92)
    var secondaryCategory = ""

    @ExcelProperty("英雄面板排序Id", index = 113)
    var order = 0

    @ExcelProperty(index = 133)
    var runeConfig = 0

    override fun toString() = Json.stringify(serializer, this)

    fun toType(): HeroType {
        val type = try {
            Class.forName("apc.eagle.common.hero.Hero$id").newInstance() as HeroType
        } catch (e: ClassNotFoundException) {
            HeroType()
        }
        type.id = id
        type.name = name
        if ("远程" in attackType) type.attackType = UnitType.RANGE
        type.baseHp = baseHp
        type.baseRegen = baseRegen
        type.baseAttack = baseAttack
        type.baseDefense = baseDefense
        type.baseMove = baseMove
        type.baseCritical = baseCritical / 10
        type.baseCriticalDamage = baseCriticalDamage / 10

        type.category = if ("辅助" in category) "辅助" else category
        type.secondaryCategory = if ("辅助" in secondaryCategory) "辅助" else secondaryCategory
        type.order = order
        type.bonusHp = bonusHp
        type.bonusRegen = bonusRegen
        type.bonusAttack = bonusAttack
        type.bonusHaste = bonusHaste / 100000
        type.bonusDefense = bonusDefense
        type.bonusMagicDefense = bonusMagicDefense
        type.defaultRuneConfig = runeConfig
        type.resetRunes()
        return type
    }

    companion object : Table<HeroInfo>() {
        override val file = "11.英雄信息表_Xavier"
        private val serializer = serializer()
        override fun invoke(row: HeroInfo, context: AnalysisContext) {
            if (row.id in HeroEnable.ids && row.id !in HeroType.idMap) {
                val type = row.toType()
                HeroType.idMap[row.id] = type
                HeroType.nameMap[row.name] = type
                println(row)
            }
        }
    }
}