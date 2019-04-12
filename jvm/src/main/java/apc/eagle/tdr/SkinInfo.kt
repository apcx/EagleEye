package apc.eagle.tdr

import apc.common.copyTo
import apc.common.plus
import apc.eagle.common.HeroType
import apc.eagle.common.Skin
import apc.eagle.common.toHero
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

@Serializable
class SkinInfo : BaseRowModel() {

    @ExcelProperty(index = 0)
    var id = 0

    @ExcelProperty(index = 1)
    var hero = 0

    @ExcelProperty(index = 2)
    @Suppress("unused")
    var heroName = ""

    @ExcelProperty(index = 3)
    var index = 0

    @ExcelProperty(index = 4)
    var name = ""

    @ExcelProperty(index = 5)
    var icon = 0

    @ExcelProperty(index = 17)
    var type = ""

    @ExcelProperty(index = 19)
    var value = 0

    override fun toString() = Json.stringify(serializer, this)

    fun toType() = Skin().also {
        it.id = id
        it.index = index
        it.name = name
        it.icon = icon
        when (type) {
            "物理攻击力" -> it.type = Skin.TYPE_ATTACK
            "法术攻击力" -> it.type = Skin.TYPE_MAGIC
            "最大生命" -> it.type = Skin.TYPE_HP
        }
    }

    companion object : Table<SkinInfo>() {
        @Suppress("SpellCheckingInspection")
        override val file = "73.皮肤配置表_Liya"
        private val serializer = SkinInfo.serializer()
        override fun invoke(row: SkinInfo, context: AnalysisContext) {
            row.hero.toHero()?.run {
                skins += row.toType()
                println(row)
            }
        }

        override fun doAfterAllAnalysed(context: AnalysisContext) {
//            copyResources()
        }

        @Suppress("unused")
        private fun copyResources() {
            val icon = Paths.get("D:\\Kings\\trunk0\\UI\\5_Dynamic\\Icon")
            val res = Paths.get("resources/head")
            res.toFile().mkdirs()
            HeroType.idMap.values.flatMap { it.skins }.forEach {
                try {
                    icon + "${it.icon}.png" copyTo res
                } catch (e: NoSuchFileException) {
                }
            }
        }
    }
}