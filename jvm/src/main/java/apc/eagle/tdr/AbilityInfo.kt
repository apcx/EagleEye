package apc.eagle.tdr

import apc.eagle.common.Ability
import apc.eagle.common.toHero
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

    @ExcelProperty(index = 52)
    var bonusCd = 0

    @ExcelProperty(index = 87)
    var type = ""

    override fun toString() = Json.stringify(serializer, this)

    fun toType() = Ability().also {
        it.id = id
        it.name = name
        it.slot = slot
        it.cd = cd
        it.bonusCd = bonusCd
        when (type) {
            "物理伤害效果" -> it.type = Ability.TYPE_PHYSICAL
            "魔法伤害效果" -> it.type = Ability.TYPE_MAGIC
            "真实伤害效果" -> it.type = Ability.TYPE_REAL
        }
    }

    companion object : Table<AbilityInfo>() {
        override val file = "21.技能基础表"
        private val serializer = AbilityInfo.serializer()
        override fun invoke(row: AbilityInfo, context: AnalysisContext) {
            val heroId = row.id / 100
            heroId.toHero()?.run {
                val type = row.toType()
                println(row)
                if (row.id == heroId * 100) {
                    type.type = Ability.TYPE_PHYSICAL
                    type.attackFactor = 100
                    type.canExpertise = true
                    type.canOrb = true
                    type.canCritical = true
                    attackAbilities += type
                } else if (row.slot > 0) {
                    type.isSpell = true
                    abilities[row.slot - 1] = type
                }
            }
        }
    }
}