package apc.eagle.fx.rune

import apc.eagle.common.Rune
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class RuneButton() : Button("（无）", ImageView().apply { isSmooth = true }) {

    var rune: Rune? = null
        set(value) {
            field = value
            update()
        }

    var count = 0
        set(value) {
            field = value
            update()
        }

    init {
        setPrefSize(115.0, 50.0)
    }

    constructor(rune: Rune, count: Int) : this() {
        this.count = count
        this.rune = rune
    }

    private fun update() {
        val imageView = graphic as ImageView
        val rune = this.rune
        if (rune == null) {
            text = "（无）"
            imageView.image = null
            imageView.fitWidth = 0.0
            imageView.fitHeight = 0.0
        } else {
            text = "${rune.name} x $count"
            imageView.image = Image("rune/${rune.id}.png")
            imageView.fitWidth = 40.0
            imageView.fitHeight = 40.0
        }
    }
}