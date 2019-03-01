package apc.eagle.fx

import apc.eagle.common.HeroType
import apc.eagle.common.Rune
import apc.eagle.tdr.RuneConfig
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Callback

class RuneStage(type: HeroType, onClose: () -> Unit) : Stage() {

    init {
        val bluePane = RunePane("蓝色符文")
        val greenPane = RunePane("黄色符文")
        val redPane = RunePane("红色符文")

        val blue = type.blueRunes.keys.first()
        bluePane.button.userData = blue
        bluePane.button.text = Rune.idMap[blue]?.name

        val green = type.greenRunes.keys.first()
        greenPane.button.userData = green
        greenPane.button.text = Rune.idMap[green]?.name

        val red = type.redRunes.keys.first()
        redPane.button.userData = red
        redPane.button.text = Rune.idMap[red]?.name

        val nameColumn = TableColumn<RuneConfig, String>("方案来自")
        nameColumn.cellValueFactory = PropertyValueFactory<RuneConfig, String>(RuneConfig::name.name)

        val blueColumn = TableColumn<RuneConfig, String>("蓝色")
        blueColumn.cellValueFactory = Callback { SimpleStringProperty(Rune.idMap[it.value.blue]?.name) }

        val greenColumn = TableColumn<RuneConfig, String>("绿色")
        greenColumn.cellValueFactory = Callback { SimpleStringProperty(Rune.idMap[it.value.green]?.name) }

        val redColumn = TableColumn<RuneConfig, String>("红色")
        redColumn.cellValueFactory = Callback { SimpleStringProperty(Rune.idMap[it.value.red]?.name) }

        val tableView = TableView<RuneConfig>()
        tableView.columns.setAll(nameColumn, blueColumn, greenColumn, redColumn)
        tableView.items.setAll(RuneConfig.configs.filter { it.heroId == type.id })
        tableView.setPrefSize(300.0, 200.0)
        tableView.setRowFactory {
            val row = TableRow<RuneConfig>()
            row.onMouseClicked = EventHandler<MouseEvent> {
                if (it.clickCount == 2) {
                    val config = row.item
                    Rune.idMap[config.blue]?.copyTo(bluePane.button)
                    Rune.idMap[config.green]?.copyTo(greenPane.button)
                    Rune.idMap[config.red]?.copyTo(redPane.button)
                }
            }
            row
        }

        val saveButton = Button("保存")
        saveButton.setOnAction {
            type.redRunes.clear()
            type.blueRunes.clear()
            type.greenRunes.clear()
            type.redRunes[redPane.button.userData as Int] = 10
            type.blueRunes[bluePane.button.userData as Int] = 10
            type.greenRunes[greenPane.button.userData as Int] = 10
        }

        val bottom = HBox() + saveButton
        bottom.alignment = Pos.CENTER

        val root = BorderPane()
        root.bottom = bottom
        root.right = tableView
        root.center = HBox() + bluePane + greenPane + redPane

        icons += "rune/3514.png".toImage()
        title = "${type.name} 铭文方案"
        scene = Scene(root)
        setOnCloseRequest { onClose() }
        show()
    }
}

class RunePane(color: String) : VBox(30.0) {

    val button = Button()

    init {
        val runes = TilePane(2.0, 2.0)
        runes.prefColumns = 2
        Rune.idMap.values.filter { it.color == color && it.level == 5 }.forEach {
            val button = Button(it.name)
            button.userData = it.id
            runes + button.copyable()
        }

        button.onCopy { string, drop ->
            val rune =
                try {
                    Rune.idMap[string.toInt()]
                } catch (e: NumberFormatException) {
                    Rune.nameMap[string]
                }
            if (rune == null) {
                false
            } else {
                if (drop) rune copyTo button
                true
            }
        }

        style = "-fx-border-color: black"
        padding = Insets(2.0)
        alignment = Pos.TOP_CENTER
        this + runes + button
    }
}

infix fun Rune.copyTo(labeled: Labeled) {
    labeled.text = name
    labeled.userData = id
}