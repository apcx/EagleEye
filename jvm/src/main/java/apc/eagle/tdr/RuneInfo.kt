package apc.eagle.tdr

import apc.eagle.common.Rune
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
class RuneInfo : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 4)
    var level = 1

    @ExcelProperty(index = 6)
    var color = ""

    @ExcelProperty(index = 9)
    @Transient
    var enable = 0

    @ExcelProperty(index = 22)
    var level1Rune = 0

    @ExcelProperty(index = 25)
    var type1 = ""

    @ExcelProperty(index = 27)
    var value1 = 0

    @ExcelProperty(index = 28)
    var type2 = ""

    @ExcelProperty(index = 30)
    var value2 = 0

    @ExcelProperty(index = 31)
    var type3 = ""

    @ExcelProperty(index = 33)
    var value3 = 0

    @ExcelProperty(index = 61)
    var name = ""

    override fun toString() = Json.stringify(serializer, this)

    fun toType() = Rune().also {
        it.id = id
        it.name = name
        it.level = level
        it.color = color
        it.setAttribute(type1, value1)
        if (type2.isNotEmpty()) {
            it.setAttribute(type2, value2)
            if (type3.isNotEmpty()) it.setAttribute(type3, value3)
        }
    }

    private fun Rune.setAttribute(type: String, value: Int) {
        when (type) {
            "最大生命" -> hp = value
            "生命回复" -> regen = value
            "物理攻击力" -> attack = value
            "攻速加成" -> attackSpeed = value / 1000
            "暴击率" -> critical = value
            "暴击效果" -> criticalDamage = value
            "法术攻击力" -> magic = value
            "冷却缩减" -> cdr = value
            "物理防御力" -> defense = value
            "法术防御力" -> magicDefense = value
            "物理护甲穿透" -> penetrate = value
            "法术护甲穿透" -> magicPenetrate = value
            "物理吸血" -> drain = value
            "法术吸血" -> magicDrain = value
            "移速" -> moveSpeed = value
        }
    }

    companion object : Table<RuneInfo>() {
        override val file = "44.符文信息表_Chad"
        private val serializer = RuneInfo.serializer()
        internal val level5Runes = mutableMapOf<Int, Int>()
        override fun invoke(row: RuneInfo, context: AnalysisContext) {
            if (row.enable > 0) {
                val type = row.toType()
                Rune.idMap[row.id] = type
                if (row.level == 5) {
                    level5Runes[row.level1Rune] = row.id
                    Rune.nameMap[row.name]
                }
                println(type)
            }
        }
    }
}