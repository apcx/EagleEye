package apc.eagle.fx.battle

import apc.eagle.common.Event
import javafx.geometry.Pos
import javafx.scene.control.TableCell
import javafx.scene.image.Image
import javafx.scene.image.ImageView

internal class HeroCell : TableCell<Event, Number>() {

    init {
        alignment = Pos.CENTER
        val imageView = ImageView()
        imageView.fitWidth = 30.0
        imageView.fitHeight = 30.0
        graphic = imageView
    }

    override fun updateItem(id: Number?, empty: Boolean) {
        super.updateItem(id, empty)
        (graphic as ImageView).image = if (id == null || id == 0) null else Image("head/$id.png")
    }
}