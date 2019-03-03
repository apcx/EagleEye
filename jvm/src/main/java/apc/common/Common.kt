package apc.common

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TableColumn
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import java.nio.file.Path

operator fun Path.plus(other: String) = resolve(other)!!

val Node.stage get() = scene.window!!

fun Window.startStage(title: String, root: Parent) {
    val stage = Stage()
    stage.initOwner(this)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.initStyle(StageStyle.UTILITY)
    stage.title = title
    stage.scene = Scene(root)
    stage.sizeToScene()
    stage.showAndWait()
}

fun Node.startStage(title: String, root: Parent) {
    stage.startStage(title, root)
}

inline operator fun <reified T : Pane> T.plus(node: Node) = apply { children += node }

fun Node.setCopyable() = apply {
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

fun TableColumn<*, *>.center() {
    style = "-fx-alignment: CENTER"
}