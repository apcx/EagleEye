package apc.eagle.fx.rune

import apc.common.center
import apc.common.plus
import apc.eagle.common.HeroType
import apc.eagle.common.Rune
import apc.eagle.common.RuneConfig
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.HBox

class RunePane(hero: HeroType) : HBox() {

    private val colorPanes = Array(3) { ColorRunePane(hero, 1 + it) }

    init {
        this + colorPanes[1] + colorPanes[2] + colorPanes[0] + initConfigs(hero)
    }

    private fun initConfigs(hero: HeroType) = TableView<RuneConfig>().apply {
        setPrefSize(320.0, 200.0)
        columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
        val nameColumn = TableColumn<RuneConfig, String>("双击使用")
        nameColumn.cellValueFactory = PropertyValueFactory<RuneConfig, String>(RuneConfig::name.name)
        nameColumn.center()
        columns.setAll(nameColumn, column(Rune.BLUE), column(Rune.GREEN), column(Rune.RED))

        val defaultConfig = RuneConfig[hero.defaultRuneConfig]
        items.add(defaultConfig)
        items.addAll(hero.recommendedRuneConfigs.filter { it.name != "小妲己" })
        setRowFactory {
            val row = TableRow<RuneConfig>()
            row.setOnMouseClicked { event ->
                if (event.clickCount == 2) {
                    val config = row.item
                    colorPanes.forEach { it.setConfig(config) }
                }
            }
            row
        }
    }

    private fun column(color: Int) = TableColumn<RuneConfig, String>().apply {
        setCellValueFactory { SimpleStringProperty(it.value.toString(color)) }
        setCellFactory { RuneCell(color) }
    }

    fun save() {
        colorPanes.forEach(ColorRunePane::save)
    }
}
