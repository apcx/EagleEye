package apc.eagle.tdr

import apc.eagle.common.HeroType
import apc.eagle.common.RuneConfig
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class RecommendedRuneConfig : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 1)
    var name = ""

    @ExcelProperty(index = 3)
    var hero = 0

    @ExcelProperty(index = 4)
    var red1 = 0

    @ExcelProperty(index = 5)
    var red2 = 0

    @ExcelProperty(index = 6)
    var red3 = 0

    @ExcelProperty(index = 7)
    var red4 = 0

    @ExcelProperty(index = 8)
    var red5 = 0

    @ExcelProperty(index = 9)
    var red6 = 0

    @ExcelProperty(index = 10)
    var red7 = 0

    @ExcelProperty(index = 11)
    var red8 = 0

    @ExcelProperty(index = 12)
    var red9 = 0

    @ExcelProperty(index = 13)
    var red10 = 0

    @ExcelProperty(index = 14)
    var blue1 = 0

    @ExcelProperty(index = 15)
    var blue2 = 0

    @ExcelProperty(index = 16)
    var blue3 = 0

    @ExcelProperty(index = 17)
    var blue4 = 0

    @ExcelProperty(index = 18)
    var blue5 = 0

    @ExcelProperty(index = 19)
    var blue6 = 0

    @ExcelProperty(index = 20)
    var blue7 = 0

    @ExcelProperty(index = 21)
    var blue8 = 0

    @ExcelProperty(index = 22)
    var blue9 = 0

    @ExcelProperty(index = 23)
    var blue10 = 0

    @ExcelProperty(index = 24)
    var green1 = 0

    @ExcelProperty(index = 25)
    var green2 = 0

    @ExcelProperty(index = 26)
    var green3 = 0

    @ExcelProperty(index = 27)
    var green4 = 0

    @ExcelProperty(index = 28)
    var green5 = 0

    @ExcelProperty(index = 29)
    var green6 = 0

    @ExcelProperty(index = 30)
    var green7 = 0

    @ExcelProperty(index = 31)
    var green8 = 0

    @ExcelProperty(index = 32)
    var green9 = 0

    @ExcelProperty(index = 33)
    var green10 = 0

    fun toType() = RuneConfig().also {
        it.id = id
        it.name = name
        it.hero = hero
        it.ids[0].addRune(red1, red2, red3, red4, red5, red6, red7, red8, red9, red10)
        it.ids[1].addRune(blue1, blue2, blue3, blue4, blue5, blue6, blue7, blue8, blue9, blue10)
        it.ids[2].addRune(green1, green2, green3, green4, green5, green6, green7, green8, green9, green10)
    }

    private fun MutableMap<Int, Int>.addRune(vararg rune: Int) {
        rune.map(RuneInfo.level5Runes::get).filterNotNull().forEach { put(it, (get(it) ?: 0) + 1) }
    }

    override fun toString() = Json.stringify(serializer, this)

    companion object : Table<RecommendedRuneConfig>() {
        override val file = "44.符文信息表_Chad"
        override val table = 9
        private val serializer = RecommendedRuneConfig.serializer()
        override fun invoke(row: RecommendedRuneConfig, context: AnalysisContext) {
            HeroType.idMap[row.hero]?.run {
                recommendedRuneConfigs += row.toType()
                println(row)
            }
        }
    }
}