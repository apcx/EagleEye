package apc.eagle.tdr

import apc.eagle.common.EquipConfig
import apc.eagle.common.toHero
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
@Suppress("unused")
class ProEquipments : BaseRowModel() {

    @ExcelProperty(index = 0)
    var heroId = 0

    @ExcelProperty(index = 1)
    var index = 1

    @ExcelProperty(index = 2)
    var name = ""

    @ExcelProperty(index = 6)
    var id1 = 0

    @ExcelProperty(index = 7)
    var id2 = 0

    @ExcelProperty(index = 8)
    var id3 = 0

    @ExcelProperty(index = 9)
    var id4 = 0

    @ExcelProperty(index = 10)
    var id5 = 0

    @ExcelProperty(index = 11)
    var id6 = 0

    @ExcelProperty(index = 15)
    var equip1 = ""

    @ExcelProperty(index = 16)
    var equip2 = ""

    @ExcelProperty(index = 17)
    var equip3 = ""

    @ExcelProperty(index = 18)
    var equip4 = ""

    @ExcelProperty(index = 19)
    var equip5 = ""

    @ExcelProperty(index = 20)
    var equip6 = ""

    override fun toString() = Json.stringify(ProEquipments.serializer, this)

    fun toType() = EquipConfig().also {
        it.name = name
        it.equips[0] = id1
        it.equips[1] = id2
        it.equips[2] = id3
        it.equips[3] = id4
        it.equips[4] = id5
        it.equips[5] = id6
    }

    companion object : Table<ProEquipments>() {
        override val file = "96.推荐装备列表_elu"
        override val table = 2
        private val serializer = ProEquipments.serializer()
        override fun invoke(row: ProEquipments, context: AnalysisContext) {
            row.heroId.toHero()?.run {
                equipConfigs[row.index + 2] = row.toType()
                println(row)
            }
        }
    }
}