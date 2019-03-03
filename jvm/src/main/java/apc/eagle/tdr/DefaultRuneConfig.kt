package apc.eagle.tdr

import apc.eagle.common.RuneConfig
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class DefaultRuneConfig : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 1)
    var name = ""

    @ExcelProperty(index = 5)
    var red = 0

    @ExcelProperty(index = 15)
    var blue = 0

    @ExcelProperty(index = 25)
    var green = 0

    fun toType() = RuneConfig().also {
        it.id = id
        it.name = "系统 - $name"
        it.red = red
        it.blue = blue
        it.green = green
    }

    override fun toString() = Json.stringify(serializer, this)

    companion object : Table<DefaultRuneConfig>() {
        override val file = "44.符文信息表_Chad"
        override val table = 2
        private val serializer = DefaultRuneConfig.serializer()
        override fun invoke(row: DefaultRuneConfig, context: AnalysisContext) {
            row.red = RuneInfo.level5Runes[row.red]!!
            row.blue = RuneInfo.level5Runes[row.blue]!!
            row.green = RuneInfo.level5Runes[row.green]!!
            RuneConfig[row.id] = row.toType()
            println(row)
        }
    }
}