package apc.eagle.fx

import apc.common.center
import apc.common.plus
import apc.common.window
import apc.eagle.common.Hero
import apc.eagle.common.HeroType
import apc.eagle.common.Rune
import apc.eagle.common.toEquip
import apc.eagle.fx.hero.HeroPane
import apc.eagle.fx.rune.RuneCell
import javafx.beans.property.SimpleStringProperty
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
import javafx.stage.StageStyle
import kotlin.math.min

class SolutionPane : VBox() {

    private val group = ToggleGroup()

    init {
        val name = TableColumn<HeroType, String>("英雄")
        name.cellValueFactory = PropertyValueFactory<HeroType, String>(HeroType::name.name)
        name.center()

        val equips = Array(6) { index ->
            val column = TableColumn<HeroType, String>((1 + index).toString())
            column.setCellValueFactory { SimpleStringProperty(it.value.equips[index].toEquip()?.name) }
            column.center()
            column
        }

        val hasteColumn = TableColumn<HeroType, String>("攻击速度 - 帧数")
        hasteColumn.setCellValueFactory {
            val type = it.value
            val hero = Hero(type)
            val haste = min(hero.expectedHaste, 2000)
            SimpleStringProperty("${"%.1f".format(haste / 10f)} - ${type.attackFrames(haste)}")
        }
        hasteColumn.center()

        val table = TableView<HeroType>()
        table.columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
        table.columns.setAll(name, *equips, column(Rune.BLUE), column(Rune.GREEN), column(Rune.RED), hasteColumn)
        table.setRowFactory {
            val row = TableRow<HeroType>()
            row.onMouseClicked = EventHandler<MouseEvent> { event ->
                if (event.clickCount == 2) {
                    val type = row.item
                    val key = type.name
                    var pane = HeroPane[key]
                    if (pane == null) {
                        pane = HeroPane(type)
                        HeroPane[key] = pane
                        val stage = Stage(StageStyle.UTILITY)
                        stage.title = "${type.name} 攻速档位"
                        stage.scene = Scene(pane)
                        stage.sizeToScene()
                        stage.setOnCloseRequest {
                            table.refresh()
                            HeroPane -= key
                        }
                        stage.show()
                    } else {
                        pane.window.requestFocus()
                    }
                }
            }
            row
        }

        group.selectedToggleProperty().addListener { _, _, newValue ->
            val allHeroes = HeroType.idMap.values.toList()
            val categoryHeroes = mutableListOf<HeroType>()
            val category = newValue.userData.toString()
            if (category == "全部") {
                categoryHeroes += allHeroes.sortedBy { it.order }
            } else {
                categoryHeroes += allHeroes.filter { it.category == category }.sortedBy { it.order }
                categoryHeroes += allHeroes.filter { it.secondaryCategory == category }.sortedBy { it.order }
            }
            categoryHeroes.forEach(HeroType::initAbilities)
            table.items.setAll(categoryHeroes)
        }

        val tabs = HBox(2.0)
        tabs.padding = Insets(2.0)
        tabs.alignment = Pos.TOP_CENTER
        tabs /* + addTab("全部") */ + addTab("坦克") + addTab("战士") + addTab("刺客") +
                addTab("法师") + addTab("射手", true) + addTab("辅助")
        this + tabs + table
    }

    private fun addTab(category: String, selected: Boolean = false) = RadioButton(category).apply {
        styleClass -= "radio-button"
        styleClass += "toggle-button"
        userData = category
        toggleGroup = group
        isSelected = selected
    }

    private fun column(color: Int) = TableColumn<HeroType, String>().apply {
        setCellValueFactory { SimpleStringProperty(it.value.runeConfig.toString(color)) }
        setCellFactory { RuneCell(color) }
    }
}