package apc.eagle.fx.equip

import apc.eagle.common.EquipConfig
import javafx.geometry.Pos
import javafx.scene.control.TableCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView

internal class EquipCell : TableCell<EquipConfig, Number>() {

    init {
        alignment = Pos.CENTER
        val imageView = ImageView()
        imageView.fitWidth = 40.0
        imageView.fitHeight = 40.0
        graphic = imageView
    }

    override fun updateItem(id: Number?, empty: Boolean) {
        super.updateItem(id, empty)
        if (id != null) (graphic as ImageView).image = Image("equip/$id.png")
    }
}