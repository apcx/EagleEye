package apc.eagle.fx

import apc.common.center
import apc.common.copyable
import apc.common.onCopy
import apc.eagle.common.Equip
import apc.eagle.common.EquipConfig
import apc.eagle.common.HeroType
import apc.eagle.common.toEquip
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox

class EquipPane(private val hero: HeroType) : BorderPane() {

    private val group = ToggleGroup()
    private var category = 1
    private val tilePane = TilePane(2.0, 2.0).apply {
        padding = Insets(2.0)
        style = "-fx-border-color: black"
        prefColumns = 4
    }

    private val buttons = Array(6) { index ->
        val button = EquipButton(hero.equips[index].toEquip())
        button.onCopy { string, drop ->
            val equip = string.toEquip()
            if (equip == null) {
                false
            } else {
                if (drop) button.equip = equip
                true
            }
        }
        button.setOnMouseClicked { if (it.clickCount == 2) button.equip = null }
        button
    }

    init {
        group.selectedToggleProperty().addListener { _, _, newValue ->
            category = newValue.userData as Int
            updateCategory()
        }

        padding = Insets(2.0)
        bottom = initCurrentEquips()
        left = initCategories()
        right = initConfigs()
        center = tilePane
    }

    private fun initCategories() = VBox(
        2.0,
        addCategory("攻击", Equip.CATEGORY_ATTACK),
        addCategory("法术", Equip.CATEGORY_MAGIC),
        addCategory("防御", Equip.CATEGORY_DEFENSE),
        addCategory("移动", Equip.CATEGORY_MOVE),
        addCategory("打野", Equip.CATEGORY_MOB),
        addCategory("辅助", Equip.CATEGORY_SUPPORT)
    ).apply { padding = Insets(2.0) }

    private fun addCategory(name: String, category: Int) = RadioButton(name).apply {
        styleClass -= "radio-button"
        styleClass += "toggle-button"
        userData = category
        isSelected = group.toggles.isEmpty()
        toggleGroup = group
    }

    private fun updateCategory() {
        tilePane.children.setAll(Equip.idMap.values.filter { it.category == category }.sortedBy { -it.price }.map {
            val button = Button(it.name)
            button.setPrefSize(80.0, 30.0)
            button.copyable()
        })
    }

    private fun initCurrentEquips() = HBox(2.0, *buttons).apply {
        padding = Insets(2.0)
        alignment = Pos.TOP_CENTER
    }

    private fun initConfigs() = TableView<EquipConfig>().apply {
        setPrefSize(520.0, 200.0)
        columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
        val nameColumn = TableColumn<EquipConfig, String>("双击使用")
        nameColumn.cellValueFactory = PropertyValueFactory<EquipConfig, String>(EquipConfig::name.name)
        nameColumn.center()

        val equips = Array(6) { index ->
            val column = TableColumn<EquipConfig, String>((1 + index).toString())
            column.setCellValueFactory { SimpleStringProperty(it.value.equips[index].toEquip()?.name) }
            column.center()
            column
        }
        columns.setAll(nameColumn, *equips)
        items.addAll(hero.equipConfigs.filterNotNull())
        setRowFactory {
            val row = TableRow<EquipConfig>()
            row.setOnMouseClicked {
                if (it.clickCount == 2)
                    row.item.equips.map(Int::toEquip).forEachIndexed { index, equip -> buttons[index].equip = equip }
            }
            row
        }
    }

    fun save() {
        hero.equips.fill(0)
        buttons.mapNotNull { it.equip }.forEachIndexed { index, it -> hero.equips[index] = it.id }
    }
}