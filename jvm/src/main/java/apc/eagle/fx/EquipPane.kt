package apc.eagle.fx

import apc.common.plus
import apc.eagle.common.Equip
import apc.eagle.common.HeroType
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.control.ToggleGroup
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox

class EquipPane(type: HeroType) : BorderPane() {

    private val group = ToggleGroup()
    private var category = 1
    private var changed = false
    private val tilePane = TilePane(2.0, 2.0).apply {
        padding = Insets(2.0)
        style = "-fx-border-color: black"
    }

    internal val customEquips = IntArray(6)

    init {
        val customEquipButtons = HBox(2.0)
        customEquipButtons.padding = Insets(2.0)
        customEquipButtons.alignment = Pos.CENTER

        type.equips.copyInto(customEquips)
        customEquips.forEach { equipId ->
            val button = Button(Equip.idMap[equipId]?.name)
            button.userData = equipId
            button.setOnDragOver {
                if (it.gestureSource != button && it.dragboard.hasString()) {
                    val string = it.dragboard.string
                    try {
                        if (string.toInt() in Equip.idMap) it.acceptTransferModes(TransferMode.COPY)
                    } catch (e: NumberFormatException) {
                        if (string in Equip.nameMap) it.acceptTransferModes(TransferMode.COPY)
                    }
                }
                it.consume()
            }
            button.setOnDragDropped {
                var success = false
                if (it.dragboard.hasString()) {
                    val string = it.dragboard.string
                    val equip = try {
                        Equip.idMap[string.toInt()]
                    } catch (e: NumberFormatException) {
                        Equip.nameMap[string]
                    }
                    equip?.run {
                        button.text = name
                        button.userData = id
                        success = true
                        changed = true
                    }
                }
                it.isDropCompleted = success
                it.consume()
            }
            customEquipButtons + button
        }

        group.selectedToggleProperty().addListener { _, _, newValue ->
            category = newValue.userData as Int
            updateList()
        }

        val saveButton = Button("保存")
        saveButton.setOnAction {
            if (changed) {
                changed = false
                customEquipButtons.children.map { it.userData as Int }.toIntArray().copyInto(customEquips)
            }
        }
        val rightBox = VBox(4.0)
        rightBox.padding = Insets(4.0)
        rightBox.alignment = Pos.BOTTOM_CENTER

        bottom = customEquipButtons
        left = VBox(
            2.0,
            addCategory("攻击", 1),
            addCategory("法术", 2),
            addCategory("防御", 3),
            addCategory("移动", 4),
            addCategory("打野", 5),
            addCategory("辅助", 7)
        ).apply { padding = Insets(2.0) }
        right = rightBox + saveButton
        center = tilePane
    }

    private fun addCategory(name: String, category: Int) = RadioButton(name).apply {
        styleClass -= "radio-button"
        styleClass += "toggle-button"
        userData = category
        isSelected = group.toggles.isEmpty()
        toggleGroup = group
    }

    private fun updateList() {
        tilePane.children.clear()
        Equip.idMap.values.filter { it.category == category && it.top > 0 }.sortedBy { -it.price }.forEach { equip ->
            val button = Button(equip.name)
            button.setOnDragDetected {
                button.startDragAndDrop(TransferMode.COPY)
                    .setContent(ClipboardContent().apply { putString(equip.id.toString()) })
                it.consume()
            }
            tilePane + button
        }
    }
}