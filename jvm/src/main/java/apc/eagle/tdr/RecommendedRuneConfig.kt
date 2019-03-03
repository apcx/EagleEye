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
    var red = 0

    @ExcelProperty(index = 14)
    var blue = 0

    @ExcelProperty(index = 24)
    var green = 0

    fun toType() = RuneConfig().also {
        it.id = id
        it.name = name
        it.hero = hero
        it.red = RuneInfo.level5Runes[red]!!
        it.blue = RuneInfo.level5Runes[blue]!!
        it.green = RuneInfo.level5Runes[green]!!
    }

    override fun toString() = Json.stringify(serializer, this)

    companion object : Table<RecommendedRuneConfig>() {
        override val file = "44.符文信息表_Chad"
        override val table = 9
        private val serializer = RecommendedRuneConfig.serializer()
        override fun invoke(row: RecommendedRuneConfig, context: AnalysisContext) {
            HeroType.idMap[row.hero]?.run { recommendedRuneConfigs += row.toType() }
        }
    }
}