package apc.eagle.tdr

import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel

internal data class HeroEnable(
    @ExcelProperty(index = 0) var id: Int = 0,
    @ExcelProperty(index = 1) var name: String = ""
) : BaseRowModel() {

    @ExcelProperty(index = 31)
    var enable = 0

    companion object : Table<HeroEnable>() {
        override val file = "英雄皮肤上下架表"
        override val table = 5
        private val names = mutableSetOf("云中君", "马超", "重炮初夏", "吕蒙")
        internal val ids = sortedSetOf<Int>()
        override fun invoke(row: HeroEnable, context: AnalysisContext) {
            if (row.enable > 0 && row.name !in names) {
                names += row.name
                ids += row.id
                println(row)
            }
        }
    }
}