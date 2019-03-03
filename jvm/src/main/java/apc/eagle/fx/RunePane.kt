package apc.eagle.fx

import apc.common.center
import apc.common.plus
import apc.eagle.common.HeroType
import apc.eagle.common.Rune
import apc.eagle.common.RuneConfig
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import kotlin.reflect.KCallable

class RunePane(type: HeroType) : BorderPane() {

    init {
        val bluePane = ColorRunePane(type, Rune.BLUE)
        val greenPane = ColorRunePane(type, Rune.GREEN)
        val redPane = ColorRunePane(type, Rune.RED)

        val blue = type.blueRunes.keys.first()
        bluePane.button.userData = blue
        bluePane.button.text = Rune.idMap[blue]?.name

        val green = type.greenRunes.keys.first()
        greenPane.button.userData = green
        greenPane.button.text = Rune.idMap[green]?.name

        val red = type.redRunes.keys.first()
        redPane.button.userData = red
        redPane.button.text = Rune.idMap[red]?.name

        val nameColumn = TableColumn<RuneConfig, String>("系统推荐")
        nameColumn.cellValueFactory = PropertyValueFactory<RuneConfig, String>(RuneConfig::name.name)
        nameColumn.center()

        val table = TableView<RuneConfig>()
        table.columns.setAll(nameColumn, column(RuneConfig::blue), column(RuneConfig::green), column(RuneConfig::red))
        val defaultConfig = RuneConfig[type.defaultRuneConfig]
        table.items.add(defaultConfig)
        table.items.addAll(type.recommendedRuneConfigs.filter { it.name != "小妲己" || it != defaultConfig })
        table.setPrefSize(230.0, 200.0)
        table.columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
        table.setRowFactory {
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

        val bottomPane = HBox() + saveButton
        bottomPane.padding = Insets(4.0)
        bottomPane.alignment = Pos.CENTER

        bottom = bottomPane
        right = table
        center = HBox() + bluePane + greenPane + redPane
    }

    private fun column(property: KCallable<*>) = TableColumn<RuneConfig, Int>().apply {
        cellValueFactory = PropertyValueFactory<RuneConfig, Int>(property.name)
        setCellFactory { RuneCell() }
    }
}

private class RuneCell : TableCell<RuneConfig, Int>() {

    init {
        alignment = Pos.CENTER
    }

    override fun updateItem(item: Int?, empty: Boolean) {
        super.updateItem(item, empty)
        item?.let(Rune.idMap::get)?.run {
            text = name
            when (color) {
                Rune.RED -> textFill = Color.DARKRED
                Rune.BLUE -> textFill = Color.MIDNIGHTBLUE
                Rune.GREEN -> textFill = Color.DARKGREEN
            }
        }
    }
}

infix fun Rune.copyTo(labeled: Labeled) {
    labeled.text = name
    labeled.userData = id
}