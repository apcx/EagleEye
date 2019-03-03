package apc.common

import javafx.scene.control.Slider

class IntSlider(min: Int, max: Int, onChange: (Int) -> Unit) : Slider(min.toDouble(), max.toDouble(), max.toDouble()) {

    init {
        blockIncrement = 1.0
        majorTickUnit = 1.0
        minorTickCount = 0
        isSnapToTicks = true
        isShowTickMarks = true
        isShowTickLabels = true
        valueProperty().addListener { _, _, newValue -> onChange(newValue.toInt()) }
    }
}