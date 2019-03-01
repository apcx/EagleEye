package apc.eagle.fx

import apc.eagle.common.Equip
import apc.eagle.common.Hero
import apc.eagle.common.HeroType
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Callback

class SolutionPane : VBox() {

    private val group = ToggleGroup()

    init {
        val nameColumn = TableColumn<HeroType, String>("英雄")
        nameColumn.cellValueFactory = PropertyValueFactory<HeroType, String>(HeroType::name.name)

        val equipColumns = Array(6) { index ->
            val column = TableColumn<HeroType, String>()
            column.cellValueFactory = Callback { SimpleStringProperty(Equip.idMap[it.value.equips[index]]?.name) }
            column
        }

        val runesColumn = TableColumn<HeroType, String>("铭文")
        runesColumn.cellValueFactory = PropertyValueFactory<HeroType, String>(HeroType::runes.name)

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

        val tableView = TableView<HeroType>()
        tableView.columns.setAll(nameColumn, *equipColumns, runesColumn, speedColumn)
        tableView.setRowFactory {
            val row = TableRow<HeroType>()
            row.onMouseClicked = EventHandler<MouseEvent> { event ->
                if (event.clickCount == 2) {
                    val type = row.item
                    val stage = Stage()
                    stage.title = "${type.name} 攻速档位"
                    stage.scene = Scene(DetailPane(type))
                    stage.show()
                }
            }
            row
        }

        group.selectedToggleProperty().addListener { _, _, newValue ->
            val category = newValue.userData.toString()
            var heroes = HeroType.idMap.values.toList()
            if (category != "全部") heroes = heroes.filter { it.category == category || it.secondaryCategory == category }
            heroes.sortedBy { it.order }.forEach { it.initSpeeds() }
            tableView.items.setAll(heroes)
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
}