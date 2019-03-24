package apc.eagle.fx

import apc.eagle.common.Equip
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.image.Image
import javafx.scene.image.ImageView

private const val NO_EQUIP = "（无装备）"

class EquipButton() : Button(NO_EQUIP, ImageView().apply { isSmooth = true }) {

    var equip: Equip? = null
        set(value) {
            field = value
            update()
        }

    init {
        setPrefSize(80.0, 70.0)
        contentDisplay = ContentDisplay.TOP
    }

    constructor(equip: Equip?) : this() {
        this.equip = equip
    }

    private fun update() {
        val imageView = graphic as ImageView
        val equip = this.equip
        if (equip == null) {
            text = NO_EQUIP
            imageView.image = null
            imageView.fitWidth = 0.0
            imageView.fitHeight = 0.0
        } else {
            text = equip.name
            imageView.image = Image("rune/1501.png")
            imageView.fitWidth = 40.0
            imageView.fitHeight = 40.0
        }
    }
}