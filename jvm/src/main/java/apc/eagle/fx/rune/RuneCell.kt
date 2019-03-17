package apc.eagle.fx.rune

import apc.eagle.common.Rune
import javafx.geometry.Pos
import javafx.scene.control.TableCell
import javafx.scene.paint.Color

class RuneCell<T>(color: Int) : TableCell<T, String>() {

    init {
        alignment = Pos.CENTER
        when (color) {
            Rune.RED -> textFill = Color.DARKRED
            Rune.BLUE -> textFill = Color.MIDNIGHTBLUE
            Rune.GREEN -> textFill = Color.DARKGREEN
        }
    }

    override fun updateItem(item: String?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item
    }
}