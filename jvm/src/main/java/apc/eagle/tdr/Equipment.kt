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

    @ExcelProperty(index = 20)
    var price = 0

    @ExcelProperty(index = 22)
    var top = 0

    @ExcelProperty(index = 23)
    @Transient
    var disable = 0

    @ExcelProperty(index = 34)
    var attack = 0

    @ExcelProperty(index = 35)
    var haste = 0

    @ExcelProperty(index = 36)
    var critical = 0

    @ExcelProperty(index = 38)
    var magic = 0

    @ExcelProperty(index = 39)
    var cdr = 0

    @ExcelProperty(index = 43)
    var defense = 0

    @ExcelProperty(index = 44)
    var magicDefense = 0

    @ExcelProperty(index = 45)
    var hp = 0

    @ExcelProperty(index = 46)
    var regen = 0

    @ExcelProperty(index = 47)
    var moveSpeed = 0

    override fun toString() = Json.stringify(serializer, this)

    fun toType() = Equip().also {
        it.id = id
        it.name = name
        it.category = category
        it.price = price
        it.top = top
        it.level = level
        it.attack = attack
        it.haste = haste / 10
        it.critical = critical / 10
        it.magic = magic
        it.cdr = cdr / 10
        it.defense = defense
        it.magicDefense = magicDefense
        it.hp = hp
        it.regen = regen
        it.moveSpeed = moveSpeed
    }

    companion object : Table<Equipment>() {
        override val file = "94.装备库表_elu"
        private val serializer = serializer()
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
            val icon =
                Paths.get("D:\\Kings\\trunk\\Project\\Assets\\Art_Resources\\UI\\4_System\\BattleEquip")
            val res = Paths.get("src/main/resources/equip")
            res.toFile().mkdirs()
            Equip.idMap.keys.forEach { icon + "$it.png" copyTo res }
        }
    }
}