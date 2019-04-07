package apc.eagle.tdr

import apc.eagle.common.SpeedModel
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
        SkinInfo.load()
        DefaultEquipments.load()
        ProEquipments.load()
        RecommendedRuneConfig.load()
        AbilityInfo.load()
        SpeedModel.initHeroes()
    }
}