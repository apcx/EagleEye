@file:Suppress("UNNECESSARY_NOT_NULL_ASSERTION")

package apc.common

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Labeled
import javafx.scene.control.TableColumn
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

operator fun Path.plus(other: String) = resolve(other)!!
operator fun Path.plus(other: Path) = resolve(other)!!

infix fun Path.copyTo(directory: Path) {
    Files.copy(this, directory + fileName, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
}

fun Path.copyTo(directory: Path, name: String) {
    Files.copy(this, directory + name, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
}

inline fun <reified T : Region> T.border() = apply {
    style = "-fx-border-color: black"
    padding = Insets(2.0)
}

val Node.window get() = scene.window!!

fun Window.startStage(title: String, root: Parent) {
    val stage = Stage(StageStyle.UTILITY)
    stage.initOwner(this)
    stage.initModality(Modality.WINDOW_MODAL)
    stage.title = title
    stage.scene = Scene(root)
    stage.sizeToScene()
    stage.showAndWait()
}

fun Node.startStage(title: String, root: Parent) {
    window.startStage(title, root)
}

inline operator fun <reified T : Pane> T.plus(node: Node) = apply { children += node }

fun Labeled.copyable() = apply {
    if (text.isNotEmpty()) {
        setOnDragDetected {
            it.consume()
            startDragAndDrop(TransferMode.COPY).setContent(ClipboardContent().apply { putString(text) })
        }
    }
}

fun Node.onCopy(check: (string: String, drop: Boolean) -> Boolean) {
    setOnDragOver {
        it.consume()
        if (it.gestureSource != this && it.dragboard.hasString() && check(it.dragboard.string, false))
            it.acceptTransferModes(TransferMode.COPY)
    }
    setOnDragDropped {
        it.consume()
        it.isDropCompleted = it.dragboard.hasString() && check(it.dragboard.string, true)
    }
}

fun TableColumn<*, *>.center() {
    style = "-fx-alignment: CENTER"
}

fun TableColumn<*, *>.right() {
    style = "-fx-alignment: CENTER-RIGHT"
}