package apc.eagle.tdr

import apc.eagle.common.HeroType
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class RuneConfig : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 1)
    var name = ""

    @ExcelProperty(index = 3)
    var heroId = 0

    @ExcelProperty(index = 4)
    var red = 0

    @ExcelProperty(index = 14)
    var blue = 0

    @ExcelProperty(index = 24)
    var green = 0

    override fun toString() = Json.stringify(serializer, this)

    companion object : Table<RuneConfig>() {
        override val file = "44.符文信息表_Chad"
        override val table = 9
        private val serializer = RuneConfig.serializer()
        val configs = mutableListOf<RuneConfig>()
        override fun invoke(row: RuneConfig, context: AnalysisContext) {
            val id = row.heroId
            HeroType.idMap[id]?.run {
                row.red = RuneInfo.level5Runes[row.red]!!
                row.blue = RuneInfo.level5Runes[row.blue]!!
                row.green = RuneInfo.level5Runes[row.green]!!
                if (row.id == id) {
                    defaultRunes[0] = row.red
                    defaultRunes[1] = row.blue
                    defaultRunes[2] = row.green
                    useDefaultRunes()
                }
                configs += row
                println(row)
            }
        }
    }
}