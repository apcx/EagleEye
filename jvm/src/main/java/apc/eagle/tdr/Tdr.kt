package apc.eagle.tdr

import apc.common.copyTo
import apc.common.plus
import apc.eagle.common.*
import apc.eagle.export.export
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isNotEmpty()) Tdr.root = Paths.get(args[0])
    Tdr.load()
}

object Tdr {
    //    var root = Paths.get("D:\\Kings\\trunk\\Tdr\\ResConvert\\data_xls")!!
    var root = Paths.get("")!!

    fun load() {
        Equipment.load()
        RuneInfo.load()
        DefaultRuneConfig.load()
        HeroEnable.load()
        HeroInfo.load()
        SkinEnable.load()
        SkinInfo.load()
        DefaultEquipments.load()
        ProEquipments.load()
        RecommendedRuneConfig.load()
        AbilityInfo.load()
        HasteModel.initHeroes()

//        exportRunes()
    }

    private fun exportHeroes() {
        val json = HeroType.idMap.values.filter { it.category == "射手" || it.name == "吕布" }
            .map {
                val icon = Paths.get("D:\\Kings\\V46_5_Dynamic\\Icon")
                val res = root.resolve("Icon")
                res.toFile().mkdirs()
                icon + "${it.skins[0].icon}.png" copyTo res
                it.export()
            }.toJson()
        root.resolve("heroes.json").toFile().printWriter().use {
            it.write(json)
        }
    }

    private fun exportEquips() {
        val list = Equip.idMap.values.filter { it.level >= 2 }.sortedBy { -it.price }
        list.forEach {
            val icon = Paths.get("D:\\Kings\\V46_4_System\\BattleEquip")
            val res = root.resolve("equip")
            res.toFile().mkdirs()
            icon + "${it.id}.png" copyTo res
        }
        val json = list.toJson()
        root.resolve("equips.json").toFile().printWriter().use { it.write(json) }
    }

    private fun exportRuneSets() {
        val json = RuneConfig.values.toJson()
        root.resolve("runeSets.json").toFile().printWriter().use { it.write(json) }
    }

    private fun exportRunes() {
        val json = Rune.idMap.values.filter { it.level >= 5 }.toJson()
        root.resolve("runes.json").toFile().printWriter().use { it.write(json) }
    }
}