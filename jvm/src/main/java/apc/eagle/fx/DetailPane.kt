package apc.eagle.fx

import apc.common.IntSlider
import apc.common.plus
import apc.common.startStage
import apc.eagle.common.Equip
import apc.eagle.common.Hero
import apc.eagle.common.HeroType
import apc.eagle.common.Rune
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.StackedAreaChart
import javafx.scene.chart.XYChart
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text

class DetailPane(private val type: HeroType) : VBox() {

    private val hero = Hero(type)
    private val xAxis = NumberAxis(-10.0, 210.0, 10.0)
    private val yAxis = NumberAxis(5.0, 19.0, 1.0).apply { isMinorTickVisible = false }
    private val attackData = XYChart.Data<Number, Number>().apply { node = Label().apply { textFill = Color.GREEN } }

    init {
        hero.level = 15
        updateChart()

        val series = XYChart.Series<Number, Number>()
        series.name = "普通攻击"
        val upper = type.attackFrames(0)
        var lower = upper
        series.data.add(XYChart.Data(0, lower))
        type.speeds.forEach {
            series.data.add(XYChart.Data((it - 1) / 10f, lower))
            lower = type.attackFrames(it)

            val x = it / 10f
            val data: XYChart.Data<Number, Number> = XYChart.Data(x, lower)
            data.node = Text(x.toString())
            series.data.add(data)
        }
        series.data.add(XYChart.Data(200, lower))
        series.data.add(attackData)

        yAxis.lowerBound = lower - 1.0
        yAxis.upperBound = upper + 1.0
        val chart = StackedAreaChart<Number, Number>(xAxis, yAxis)
        chart.data.add(series)
        chart.title = "${type.name} 攻速档位"
        chart.prefWidth = 600.0

        val slider = IntSlider(1, 15) {
            hero.level = it
            updateChart()
        }

        val equipsButton = Button(type.equips.joinToString { id -> Equip.idMap[id]!!.name })
        equipsButton.setOnAction {
            val pane = EquipPane(type)
            startStage("${type.name} 装备方案", pane)
            pane.customEquips.copyInto(type.equips)
            equipsButton.text = type.equips.joinToString { id -> Equip.idMap[id]!!.name }
            updateChart()
        }

        val runesButton = Button(type.runes.map { "${Rune.idMap[it.key]?.name} x ${it.value}" }.joinToString("\n"))
        runesButton.setOnAction {
            startStage("${type.name} 铭文方案", RunePane(type))
            runesButton.text = type.runes.map { "${Rune.idMap[it.key]?.name} x ${it.value}" }.joinToString("\n")
            updateChart()
        }

        padding = Insets(4.0)
        alignment = Pos.CENTER
        spacing = 4.0
        this + runesButton + equipsButton + Label("英雄等级") + slider + chart
    }

    private fun updateChart() {
        hero.updateAttributes()
        val x = hero.expectedSpeed / 10f
        val y = type.attackFrames(hero.expectedSpeed)
        xAxis.label = "攻速加成 +$x%"
        yAxis.label = "每次普通攻击间隔 $y 帧"
        attackData.xValue = x
        attackData.yValue = y
        (attackData.node as Labeled).text = x.toString()
    }
}