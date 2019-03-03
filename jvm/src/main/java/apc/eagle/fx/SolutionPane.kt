package apc.eagle.fx

import apc.common.center
import apc.common.plus
import apc.common.startStage
import apc.eagle.common.*
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.util.Callback
import kotlin.reflect.KCallable

class SolutionPane : VBox() {

    private val group = ToggleGroup()

    init {
        val nameColumn = TableColumn<HeroType, String>("英雄")
        nameColumn.cellValueFactory = PropertyValueFactory<HeroType, String>(HeroType::name.name)
        nameColumn.center()

        val equipColumns = Array(6) { index ->
            val column = TableColumn<HeroType, String>()
            column.cellValueFactory = Callback { SimpleStringProperty(Equip.idMap[it.value.equips[index]]?.name) }
            column.center()
            column
        }

        val speedColumn = TableColumn<HeroType, String>("攻击速度 - 帧数")
        speedColumn.cellValueFactory =
            Callback<TableColumn.CellDataFeatures<HeroType, String>, ObservableValue<String>> {
                object : SimpleStringProperty(it.value, "") {
                    override fun getValue(): String {
                        val type = bean as HeroType
                        val hero = Hero(type)
                        hero.level = 15
                        hero.updateAttributes()
                        return "${String.format(
                            "%.1f",
                            hero.expectedSpeed / 10f
                        )} - ${type.attackFrames(hero.expectedSpeed)}"
                    }
                }
            }
        speedColumn.center()

        val tableView = TableView<HeroType>()
        tableView.columns.setAll(
            nameColumn, *equipColumns,
            column(HeroType::blueRunes, 2),
            column(HeroType::greenRunes, 3),
            column(HeroType::redRunes, 1),
            speedColumn
        )
        tableView.columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
        tableView.setRowFactory {
            val row = TableRow<HeroType>()
            row.onMouseClicked = EventHandler<MouseEvent> { event ->
                if (event.clickCount == 2) {
                    val type = row.item
                    startStage("${type.name} 攻速档位", DetailPane(type))
                }
            }
            row
        }

        group.selectedToggleProperty().addListener { _, _, newValue ->
            val category = newValue.userData.toString()
            var types = HeroType.idMap.values.toList()
            if (category != "全部") types = types.filter { it.category == category || it.secondaryCategory == category }
            types.sortedBy { it.order }.forEach {
                it.resetRunes()
                it.initSpeeds()
            }
            tableView.items.setAll(types)
        }

        val tabs = HBox(2.0)
        tabs.padding = Insets(2.0)
        tabs.alignment = Pos.CENTER
        tabs + addTab("全部") + addTab("坦克") + addTab("战士") +
                addTab("刺客") + addTab("法师") + addTab("射手", true)
        this + tabs + tableView
    }

    private fun addTab(category: String, selected: Boolean = false) = RadioButton(category).apply {
        styleClass -= "radio-button"
        styleClass += "toggle-button"
        userData = category
        toggleGroup = group
        isSelected = selected
    }


    private fun column(property: KCallable<*>, color: Int) = TableColumn<HeroType, Map<Int, Int>>().apply {
        cellValueFactory = PropertyValueFactory<HeroType, Map<Int, Int>>(property.name)
        setCellFactory { RuneConfigCell(color) }
    }
}

private class RuneConfigCell(color: Int) : TableCell<HeroType, Map<Int, Int>>() {

    init {
        alignment = Pos.CENTER
        when (color) {
            Rune.RED -> textFill = Color.DARKRED
            Rune.BLUE -> textFill = Color.MIDNIGHTBLUE
            Rune.GREEN -> textFill = Color.DARKGREEN
        }
    }

    override fun updateItem(item: Map<Int, Int>?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.map { "${it.key.toRune()?.name} x ${it.value}" }?.joinToString("\n")
    }
}