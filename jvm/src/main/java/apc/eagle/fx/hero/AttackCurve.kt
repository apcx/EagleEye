package apc.eagle.fx.hero

import apc.eagle.common.HeroType
import javafx.application.Platform
import javafx.scene.chart.XYChart
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.image.ImageView
import javafx.scene.text.Text
import kotlin.math.max
import kotlin.math.min

internal class AttackCurve(private val type: HeroType, private val index: Int) {

    internal val series = XYChart.Series<Number, Number>()
    internal val maxFrames: Int
    internal val minFrames: Int
    private val attackData = XYChart.Data<Number, Number>().apply { node = Label("X") }
    private val ironData = XYChart.Data<Number, Number>()
    private val stormData = XYChart.Data<Number, Number>()
    private val tempData = XYChart.Data<Number, Number>().apply { node = Label() }
    private val name: String

    init {
        val ironView = ImageView("equip/1333.png")
        ironView.fitWidth = 30.0
        ironView.fitHeight = 30.0
        ironData.node = ironView

        val stormView = ImageView("equip/1136.png")
        stormView.fitWidth = 30.0
        stormView.fitHeight = 30.0
        stormData.node = stormView

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

    internal fun update(haste: Int, storm: Boolean, level: Int) {
        val speeds = type.attackAbilities[index].speeds
        val ironSpeed = haste - 300
        ironData.update(!storm && ironSpeed > 0, ironSpeed, max(0, 1 + speeds.indexOfLast { it <= ironSpeed }) * 2)

        val index200 = speeds.size * 2 + 1
        stormData.update(storm, haste + 300, index200)

        val tempHaste = type.tempHaste(level)
        if (tempHaste > (if (storm) 300 else 0)) {
            tempData.update(true, haste + tempHaste, index200)
            (tempData.node as Labeled).text = "${type.tempHasteName}\n+${(tempHaste / 10)}%"
        } else {
            tempData.update(false, 0, 0)
        }

        series.name = "$name${attackData.update(haste)}帧"
    }

    private fun XYChart.Data<Number, Number>.update(visible: Boolean, haste: Int, index: Int) {
        if (visible) {
            update(haste)
            Platform.runLater { if (this !in series.data) series.data.add(index, this) }
        } else if (this in series.data) {
            series.data.remove(this)
        }
    }

    private fun XYChart.Data<Number, Number>.update(rawHaste: Int): Int {
        val haste = min(rawHaste, 2000)
        val y = type.attackFrames(haste, index)
        xValue = haste / 10f
        yValue = y
        return y
    }
}