package apc.eagle.tdr

import apc.eagle.common.toHero
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.metadata.BaseRowModel

internal class SkinEnable : BaseRowModel() {
    @Transient
    @ExcelProperty(index = 1)
    var hero = 0

    @ExcelProperty(index = 2)
    var heroName = ""

    @Transient
    @ExcelProperty(index = 3)
    var index = 0

    @ExcelProperty(index = 4)
    var name = ""

    @ExcelProperty(index = 6)
    var limited = ""

    @Transient
    var limitedIndex = 0

    @ExcelProperty(index = 8)
    var tag = ""

    @Transient
    var tagIndex = 0

    @ExcelProperty(index = 10)
    var source = ""

    @ExcelProperty(index = 11)
    var quality = ""

    @Transient
    var qualityIndex = 0

    @ExcelProperty(index = 17)
    var canBuy = ""

    @ExcelProperty(index = 18)
    var price = 0

    @Transient
    @ExcelProperty(index = 37)
    var enable = 0

    override fun toString() =
        "$heroName\t$name\t$quality\t${if (limitedIndex == -1) "限定" else ""}\t$tag\t$price\t$source"

    companion object : Table<SkinEnable>() {
        override val file = "英雄皮肤上下架表"
        override val table = 6
        private val all = mutableListOf<SkinEnable>()
        override fun invoke(row: SkinEnable, context: AnalysisContext) {
            if (row.hero in HeroEnable.ids && row.enable > 0 && row.index > 0) {
                @Suppress("SpellCheckingInspection")
                row.qualityIndex = when {
                    "内测专属" in row.source -> 1
                    "label_rongyaodiancang" in row.tag -> {
                        row.tag = "荣耀典藏"
                        row.price = 14440
                        row.limited = ""
                        row.source = ""
                        2
                    }
                    row.quality == "S+" -> 3
                    row.price >= 2888 -> 4
                    row.quality == "S" -> 5
                    row.quality == "A" -> 6
                    row.quality == "B" -> 7
                    else -> 0
                }
                if ("label_limited_zhandui" in row.tag) {
                    row.tag = "战队赛限定"
                    row.limited = ""
                    row.source = ""
                }
                if ("贵族" in row.source) row.limited = ""
                if (row.canBuy == "是") row.limited = ""
                if (row.limited == "是") row.limitedIndex = -1
                if (row.tag.isNotEmpty()) row.tagIndex = -1
                all += row
            }
        }

        override fun doAfterAllAnalysed(context: AnalysisContext) {
            all.sortedWith(Comparator { a, b ->
                val orderA = a.hero.toHero()!!.order
                val orderB = b.hero.toHero()!!.order
                when {
                    orderA != orderB -> orderA - orderB
                    a.hero != b.hero -> a.hero - b.hero
                    a.qualityIndex != b.qualityIndex -> a.qualityIndex - b.qualityIndex
                    a.limitedIndex != b.limitedIndex -> a.limitedIndex - b.limitedIndex
                    a.tagIndex != b.tagIndex -> a.tagIndex - b.tagIndex
                    a.price != b.price -> b.price - a.price
                    else -> b.index - a.index
                }
            }).forEach(::println)
        }
    }
}