package apc.eagle.fx

import apc.eagle.common.GameData.RES_VERSION
import apc.eagle.tdr.Tdr
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isNotEmpty()) Tdr.root = Paths.get(args[0])
    Tdr.load()
    Application.launch(App::class.java)
}

class App : Application() {
    override fun start(primaryStage: Stage) {
        primaryStage.initStyle(StageStyle.UTILITY)
        primaryStage.title = "鹰眼神射 - Res $RES_VERSION - beta"
        primaryStage.scene = Scene(SolutionPane())
        primaryStage.show()
    }
}