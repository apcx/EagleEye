package apc.eagle.tdr

import apc.common.copyTo
import apc.common.plus
import apc.eagle.common.Equip
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import java.nio.file.Paths

@Serializable
class Equipment : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 3)
    @Transient
    var mode = 0

    @ExcelProperty(index = 5)
    var name = ""

    @ExcelProperty(index = 7)
    var category = 0

    @ExcelProperty(index = 10)
    var level = 0

    @ExcelProperty(index = 19)
    var price = 0

    @ExcelProperty(index = 21)
    var top = 0

    @ExcelProperty(index = 22)
    @Transient
    var disable = 0

    @ExcelProperty(index = 30)
    var attack = 0

    @ExcelProperty(index = 31)
    var attackSpeed = 0

    @ExcelProperty(index = 32)
    var critical = 0

    @ExcelProperty(index = 34)
    var magic = 0

    @ExcelProperty(index = 35)
    var cdr = 0

    @ExcelProperty(index = 39)
    var defense = 0

    @ExcelProperty(index = 40)
    var magicDefense = 0

    @ExcelProperty(index = 41)
    var hp = 0

    @ExcelProperty(index = 42)
    var regen = 0

    @ExcelProperty(index = 43)
    var moveSpeed = 0

    override fun toString() = Json.stringify(serializer, this)

    fun toType() = Equip().also {
        it.id = id
        it.name = name
        it.category = category
        it.price = price
        it.top = top
        it.attack = attack
        it.attackSpeed = attackSpeed / 10
        it.critical = critical
        it.magic = magic
        it.cdr = cdr
        it.defense = defense
        it.magicDefense = magicDefense
        it.hp = hp
        it.regen = regen
        it.moveSpeed = moveSpeed
    }

    companion object : Table<Equipment>() {
        override val file = "94.装备库表_elu"
        private val serializer = Equipment.serializer()
        private val names = mutableSetOf<String>()
        override fun invoke(row: Equipment, context: AnalysisContext) {
            if (row.mode == 0 && row.disable == 0 && row.price > 0 && row.name !in names) {
                names += row.name
                val type = row.toType()
                Equip.idMap[row.id] = type
                Equip.nameMap[row.name] = type
                println(row)
            }
        }

        override fun doAfterAllAnalysed(context: AnalysisContext) {
//            copyResources()
        }

        @Suppress("unused")
        private fun copyResources() {
            val icon = Paths.get("D:\\Kings\\trunk\\UI\\5_Dynamic\\Icon")
            val res = Paths.get("resources")
            res.toFile().mkdirs()
            Equip.idMap.keys.forEach { icon + "$it.png" copyTo res + "equip" }
        }
    }
}