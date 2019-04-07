package apc.eagle.fx.hero

import apc.common.IntSlider
import apc.common.border
import apc.common.plus
import apc.common.startStage
import apc.eagle.common.Hero
import apc.eagle.common.HeroType
import apc.eagle.common.Skin
import apc.eagle.common.toEquip
import apc.eagle.fx.battle.BattlePane
import apc.eagle.fx.equip.EquipButton
import apc.eagle.fx.equip.EquipPane
import apc.eagle.fx.rune.RuneButton
import apc.eagle.fx.rune.RunePane
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.chart.AreaChart
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text

class HeroPane(private val type: HeroType) : VBox(4.0) {

    private val hero = Hero(type)
    private val equipBox = HBox(2.0)
    private val passiveLabel = Label().border().apply { padding = Insets(2.0) }
    private val xAxis = NumberAxis(-10.0, 210.0, 10.0)
    private val attackCurves = mutableListOf<AttackCurve>()

    private val runeBoxes = Array(3) {
        val box = VBox(2.0)
        box.prefHeight = 105.0
        box.addEventFilter(MouseEvent.MOUSE_CLICKED) {
            val pane = RunePane(type)
            startStage("${type.name} 铭文方案", pane)
            pane.save()
            resetRuneButtons()
            updateChart()
        }
        box
    }

    init {
        padding = Insets(4.0)
        alignment = Pos.TOP_CENTER
        this + initRunes() + initEquips() + initLevel() + initChart() + initBattle()
    }

    private fun initRunes() = HBox(16.0).apply {
        alignment = Pos.CENTER
        val head = ImageView("rune/1501.png")
//        val head = ImageView("head/${type.preferredIcon}.png")
        head.fitWidth = 50.0
        head.fitHeight = 50.0
        val box = VBox(4.0, Label(type.name).apply { graphic = head })
        box.alignment = Pos.CENTER
        if (type.skins.size >= 2) {
            val skin = when (type.skins.last().type) {
                Skin.TYPE_ATTACK -> "物理攻击+10"
                Skin.TYPE_MAGIC -> "法术攻击+10"
                Skin.TYPE_HP -> "最大生命+120"
                else -> ""
            }
            box + Label(skin).border().apply { padding = Insets(2.0) }
        }

        resetRuneButtons()
        this + box + runeBoxes[1] + runeBoxes[2] + runeBoxes[0]
    }

    private fun resetRuneButtons() {
        runeBoxes.forEachIndexed { index, box ->
            box.children.setAll(type.runeConfig.toRunes(1 + index).map { RuneButton(it.first, it.second) })
        }
    }

    private fun initEquips() = equipBox.apply {
        alignment = Pos.TOP_CENTER
        resetEquipButtons()
        addEventFilter(MouseEvent.MOUSE_CLICKED) {
            val pane = EquipPane(type)
            startStage("${type.name} 装备方案", pane)
            pane.save()
            resetEquipButtons()
            updateChart()
        }
    }

    private fun resetEquipButtons() {
        val buttons = mutableListOf<Node>()
        type.equips.map(Int::toEquip).filterNotNull().forEach { buttons.add(EquipButton(it)) }
        if (buttons.isEmpty()) buttons.add(EquipButton())
        equipBox.children.setAll(buttons)
    }

    private fun initLevel() = HBox(8.0).apply {
        alignment = Pos.CENTER
        val slider = IntSlider(1, 15) {
            hero.level = it
            updateChart()
        }
        slider.prefWidth = 450.0
        this + Text("英雄等级") + slider + passiveLabel
    }

    private fun initChart(): Node {
        type.attackAbilities.forEachIndexed { index, _ -> attackCurves += AttackCurve(type, index) }
        updateChart()

        val lower = attackCurves.minBy { it.minFrames }!!.minFrames - 1.0
        val upper = attackCurves.maxBy { it.maxFrames }!!.maxFrames + 1.0
        val yAxis = NumberAxis("每次普通攻击间隔的帧数", lower, upper, 1.0)
        yAxis.isMinorTickVisible = false

        val chart = AreaChart<Number, Number>(xAxis, yAxis)
        chart.prefWidth = 600.0
        if (type.attackAbilities.size > 1) chart.prefHeight = 600.0
        chart.data.addAll(attackCurves.map { it.series })
        return chart
    }

    private fun updateChart() {
        hero.updateAttributes()
        val level = hero.level
        val passiveSpeed = type.passiveSpeed(level)
        if (passiveSpeed > 0) {
            passiveLabel.isVisible = true
            passiveLabel.text = "${type.passiveSpeedName}\n攻速+${passiveSpeed / 10}%"
        } else {
            passiveLabel.isVisible = false
        }
        val speed = hero.expectedSpeed
        val storm = type.attackAbilities[0].canCritical && 1136 in hero.type.equips
        xAxis.label = "攻速加成 +${speed / 10f}%"
        attackCurves.forEach { it.update(speed, storm, level) }
    }

    private fun initBattle() = Button("攻击庄周").apply {
        padding = Insets(16.0)
        setOnAction { startStage("", BattlePane(type)) }
    }
}