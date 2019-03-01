package apc.eagle.fx

import apc.eagle.common.GameData.RES_VERSION
import apc.eagle.tdr.Tdr
import javafx.application.Application
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isNotEmpty()) Tdr.root = Paths.get(args[0])
    Tdr.load()
    Application.launch(App::class.java)
}

class App : Application() {
    override fun start(primaryStage: Stage) {
        primaryStage.title = "鹰眼神射 - Res $RES_VERSION - alpha01"
        primaryStage.scene = Scene(SolutionPane(), 800.0, 400.0)
        primaryStage.show()
    }
}

fun String.toImage() = ClassLoader.getSystemClassLoader().getResourceAsStream(this).use(::Image)

inline operator fun <reified T : Pane> T.plus(node: Node) = apply { children += node }

fun Node.copyable() = apply {
    setOnDragDetected {
        startDragAndDrop(TransferMode.COPY).setContent(ClipboardContent().apply { putString(userData.toString()) })
        it.consume()
    }
}

fun Node.onCopy(check: (String, Boolean) -> Boolean) {
    setOnDragOver {
        if (it.gestureSource != this && it.dragboard.hasString() && check(it.dragboard.string, false))
            it.acceptTransferModes(TransferMode.COPY)
        it.consume()
    }
    setOnDragDropped {
        it.isDropCompleted = it.dragboard.hasString() && check(it.dragboard.string, true)
        it.consume()
    }
}