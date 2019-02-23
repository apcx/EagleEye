package apc.eagle.tdr

import apc.eagle.common.HeroType
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class DefaultRunes : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 4)
    var red = 0

    @ExcelProperty(index = 14)
    var blue = 0

    @ExcelProperty(index = 24)
    var green = 0

    override fun toString() = Json.stringify(serializer, this)

    companion object : Table<DefaultRunes>() {
        override val file = "44.符文信息表_Chad"
        override val table = 9
        private val serializer = DefaultRunes.serializer()
        override fun invoke(row: DefaultRunes, context: AnalysisContext) {
            HeroType.idMap[row.id]?.run {
                defaultRunes[0] = RuneInfo.level5Runes[row.red]!!
                defaultRunes[1] = RuneInfo.level5Runes[row.blue]!!
                defaultRunes[2] = RuneInfo.level5Runes[row.green]!!
                println(row)
            }
        }
    }
}