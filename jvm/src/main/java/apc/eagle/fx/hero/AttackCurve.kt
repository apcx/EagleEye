package apc.eagle.fx.hero

import apc.eagle.common.HeroType
import javafx.application.Platform
import javafx.scene.chart.XYChart
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import kotlin.math.min

internal class AttackCurve(private val type: HeroType, private val index: Int) {

    internal val series = XYChart.Series<Number, Number>()
    internal val maxFrames: Int
    internal val minFrames: Int
    private val attackData = XYChart.Data<Number, Number>().apply { node = Label("X") }
    private val slowerData = XYChart.Data<Number, Number>()
    private val quickerData = XYChart.Data<Number, Number>()
    private val name: String

    init {
        val slowerImageView = ImageView("equip/1333.png")
        slowerImageView.fitWidth = 30.0
        slowerImageView.fitHeight = 30.0
        slowerData.node = slowerImageView

        val quickerImageView = ImageView("equip/1136.png")
        quickerImageView.fitWidth = 30.0
        quickerImageView.fitHeight = 30.0
        quickerData.node = quickerImageView

        var y = type.attackFrames(0, index)
        maxFrames = y
        val dataList = mutableListOf(XYChart.Data<Number, Number>(0, y))
        val ability = type.attackAbilities[index]
        name = if (index == 0) "攻击" else ability.name
        ability.speeds.forEach {
            dataList.add(XYChart.Data((it - 1) / 10f, y))
            val x = it / 10f
            y = type.attackFrames(it, index)
            dataList.add(XYChart.Data<Number, Number>(x, y).apply { node = Text(x.toString()) })
        }
        minFrames = y
        dataList.add(attackData)
        dataList.add(XYChart.Data(200, y))
        series.data.addAll(dataList)
    }

    internal fun update(speed: Int, shadowEdge: Boolean) {
        if (shadowEdge) {
            if (slowerData in series.data) series.data.remove(slowerData)
            quickerData.update(speed + 300)
            if (quickerData !in series.data) Platform.runLater { series.data.add(series.data.size - 1, quickerData) }
        } else {
            if (quickerData in series.data) series.data.remove(quickerData)
            slowerData.update(speed - 300)
            if (slowerData !in series.data) Platform.runLater { series.data.add(0, slowerData) }
        }
        series.name = "$name${attackData.update(speed)}帧"
    }

    private fun XYChart.Data<Number, Number>.update(rawSpeed: Int): Int {
        val speed = min(rawSpeed, 2000)
        val x = speed / 10f
        val y = type.attackFrames(speed, index)
        xValue = x
        yValue = y
        return y
    }
}