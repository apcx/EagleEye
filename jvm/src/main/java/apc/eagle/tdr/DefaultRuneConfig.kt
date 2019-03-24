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
        it.name = "系统 - $name"
        it.ids[0][RuneInfo.level5Runes[red]!!] = 10
        it.ids[1][RuneInfo.level5Runes[blue]!!] = 10
        it.ids[2][RuneInfo.level5Runes[green]!!] = 10
        RuneConfig[id] = it
    }

    override fun toString() = Json.stringify(serializer, this)

    companion object : Table<DefaultRuneConfig>() {
        override val file = "44.符文信息表_Chad"
        override val table = 2
        private val serializer = DefaultRuneConfig.serializer()
        override fun invoke(row: DefaultRuneConfig, context: AnalysisContext) {
            row.toType()
            println(row)
        }
    }
}