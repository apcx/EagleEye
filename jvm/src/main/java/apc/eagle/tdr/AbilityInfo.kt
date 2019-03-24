package apc.eagle.tdr

import apc.eagle.common.Ability
import apc.eagle.common.HeroType
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class AbilityInfo : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 1)
    var name = ""

    @ExcelProperty(index = 2)
    var slot = 0

    @ExcelProperty(index = 49)
    var cd = 0

    override fun toString() = Json.stringify(serializer, this)

    fun toType() = Ability().also {
        it.id = id
        it.name = name
        it.slot = slot
        it.cd = cd
    }

    companion object : Table<AbilityInfo>() {
        override val file = "21.技能基础表"
        private val serializer = AbilityInfo.serializer()
        override fun invoke(row: AbilityInfo, context: AnalysisContext) {
            val heroId = row.id / 100
            HeroType.idMap[heroId]?.run {
                val type = row.toType()
                Ability[row.id] = type
                println(type)
                if (row.id == heroId * 100)
                    attackAbilities += type
                else if (row.slot > 0)
                    abilities[row.slot - 1] = row.id
            }
        }
    }
}