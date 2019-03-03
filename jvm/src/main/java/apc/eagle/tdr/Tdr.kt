package apc.eagle.tdr

import apc.common.plus
import apc.eagle.common.SpeedModel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

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
        DefaultEquipments.load()
        RecommendedRuneConfig.load()
        AbilityInfo.load()
        SpeedModel.initHeroes()
    }

    @Suppress("unused")
    private fun copyResources() {
        val icon = Paths.get("D:\\Kings\\trunk\\UI\\5_Dynamic\\Icon")
        val res = Paths.get("resources")
        res.toFile().mkdirs()
        HeroEnable.ids.forEach {
            val name = "30${it}0.png"
            Files.copy(
                icon + name,
                res + "hero" + name,
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
            )
        }
    }
}