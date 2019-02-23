package apc.eagle.tdr

import apc.eagle.common.HeroType
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
@Suppress("unused")
class DefaultEquipments : BaseRowModel() {

    @ExcelProperty(index = 0)
    var heroId = 0

    @ExcelProperty(index = 1)
    var index = 1

    @ExcelProperty(index = 4)
    var id1 = 0

    @ExcelProperty(index = 5)
    var id2 = 0

    @ExcelProperty(index = 6)
    var id3 = 0

    @ExcelProperty(index = 7)
    var id4 = 0

    @ExcelProperty(index = 8)
    var id5 = 0

    @ExcelProperty(index = 9)
    var id6 = 0

    @ExcelProperty(index = 11)
    var heroName = ""

    @ExcelProperty(index = 13)
    var equip1 = ""

    @ExcelProperty(index = 14)
    var equip2 = ""

    @ExcelProperty(index = 15)
    var equip3 = ""

    @ExcelProperty(index = 16)
    var equip4 = ""

    @ExcelProperty(index = 17)
    var equip5 = ""

    @ExcelProperty(index = 18)
    var equip6 = ""

    override fun toString() = Json.stringify(DefaultEquipments.serializer, this)

    fun copyTo(equips: Array<IntArray>) {
        val slot = equips[index - 1]
        slot[0] = id1
        slot[1] = id2
        slot[2] = id3
        slot[3] = id4
        slot[4] = id5
        slot[5] = id6
    }

    companion object : Table<DefaultEquipments>() {
        override val file = "96.推荐装备列表_elu"
        private val serializer = DefaultEquipments.serializer()
        override fun invoke(row: DefaultEquipments, context: AnalysisContext) {
            HeroType.idMap[row.heroId]?.run {
                row.copyTo(defaultEquips)
                if (row.index == 1) useDefaultEquips()
                println(row)
            }
        }
    }
}