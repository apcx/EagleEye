package apc.eagle.tdr

import apc.eagle.common.Hero
import apc.eagle.common.HeroType
import apc.eagle.common.SpeedModel
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isNotEmpty()) Tdr.root = Paths.get(args[0])
    Tdr.load()
}

object Tdr {

    var root = Paths.get("D:\\Kings\\trunk\\Tdr\\ResConvert\\data_xls")!!

    fun load() {
        Equipment.load()
        RuneInfo.load()
        HeroEnable.load()
        HeroInfo.load(HeroEnable.ids)
        DefaultEquipments.load()
        DefaultRunes.load()
        AbilityInfo.load()
        SpeedModel.initHeroes()

        HeroType.idMap.values.filter { it.category == "射手" }.sortedBy { it.order }.forEach {
            it.buildSpeeds()
            val hero = Hero(it)
            hero.level = 15
            hero.updateAttributes()
            println("${it.name} ${hero.baseAttackSpeed} ${hero.attackFrames(hero.baseAttackSpeed)} $it")
        }
    }
}