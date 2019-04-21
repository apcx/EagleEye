package apc.eagle.fx.hero

import apc.common.*
import apc.eagle.common.*
import apc.eagle.fx.battle.BattlePane
import apc.eagle.fx.equip.EquipButton
import apc.eagle.fx.equip.EquipPane
import apc.eagle.fx.rune.RuneButton
import apc.eagle.fx.rune.RunePane
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.chart.AreaChart
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.StageStyle

class HeroPane(private val type: HeroType) : VBox(4.0) {

    private val hero = Hero(type)
    private val equipBox = HBox(2.0)
    private val priceLabel = Label().border()
    private val passiveLabel = Label().border()
    private val xAxis = NumberAxis(-10.0, 210.0, 10.0)
    private val attackCurves = mutableListOf<AttackCurve>()
    private val criticalLabels = Array(2) { Label() }
    private val avgLabels = Array(2) { Label() }
    private val extraLabel = Label()

    private val runeBoxes = Array(3) {
        val box = VBox(2.0)
        box.prefHeight = 105.0
        box.addEventFilter(MouseEvent.MOUSE_CLICKED) {
            val pane = RunePane(type)
            startStage("${type.name} 铭文方案", pane)
            pane.save()
            resetRuneButtons()
            update()
        }
        box
    }

    init {
        padding = Insets(4.0)
        alignment = Pos.TOP_CENTER
        this + initRunes() + initEquips() + initLevel() + initGrid() + initChart() + initBattle()
    }

    private fun initRunes() = HBox(16.0).apply {
        alignment = Pos.CENTER
        val head = ImageView("head/${type.preferredIcon}.png")
        head.fitWidth = 64.0
        head.fitHeight = 64.0
        val button = Button(type.name).copyable()
        button.graphic = head
        button.onCopy { string, drop ->
            val attacker = get(string)
            if (attacker == null || attacker.type == type) {
                false
            } else {
                if (drop) {
                    val key = "${attacker.type.name} vs ${type.name}"
                    var pane = BattlePane[key]
                    if (pane == null) {
                        pane = BattlePane(attacker.hero, hero)
                        BattlePane[key] = pane
                        val stage = Stage(StageStyle.UTILITY)
                        stage.scene = Scene(pane)
                        stage.sizeToScene()
                        stage.setOnCloseRequest { BattlePane -= key }
                        stage.show()
                    } else {
                        pane.window.requestFocus()
                    }
                }
                true
            }
        }
        val box = VBox(4.0, button)
        box.alignment = Pos.CENTER
        if (type.skins.size >= 2) {
            val skin = when (type.skins.last().type) {
                Skin.TYPE_ATTACK -> "物理攻击+10"
                Skin.TYPE_MAGIC -> "法术攻击+10"
                Skin.TYPE_HP -> "最大生命+120"
                else -> ""
            }
            box + Label(skin).border()
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
        alignment = Pos.CENTER
        resetEquipButtons()
        addEventFilter(MouseEvent.MOUSE_CLICKED) {
            val pane = EquipPane(type)
            startStage("${type.name} 装备方案", pane)
            pane.save()
            resetEquipButtons()
            update()
        }
    }

    private fun resetEquipButtons() {
        val buttons = mutableListOf<Node>()
        type.equips.map(Int::toEquip).filterNotNull().forEach { buttons.add(EquipButton(it)) }
        if (buttons.isEmpty()) buttons.add(EquipButton())
        equipBox.children.clear()
        equipBox.children += priceLabel
        equipBox.children += buttons
    }

    private fun initLevel() = HBox(8.0).apply {
        alignment = Pos.CENTER
        val slider = IntSlider(1, 15) {
            hero.level = it
            update()
        }
        slider.prefWidth = 450.0
        this + Text("英雄等级") + slider + passiveLabel
    }

    private fun initGrid() = GridPane().apply {
        alignment = Pos.TOP_CENTER
        var row = 0
        add("无双", criticalLabels[0], row)
        add("祸源", criticalLabels[1], row + 1)
        val runeLabel = Label("再多一个【5级铭文】对普通攻击的增益")
        runeLabel.padding = Insets(16.0)
        add(runeLabel, 2, row, 1, 2)
        row += 2

        if (hero.avgAttack[0] > 0) {
            add("攻击", avgLabels[0], row)
            add(type.specialAttackName, avgLabels[1], row + 1)
            val label = Label("这两行的数值为【攻击力】计入暴击后的平均数学期望值")
            label.padding = Insets(16.0)
            add(label, 2, row, 1, 2)
            row += 2
        }
        add("额外攻击", extraLabel, row)
    }


    private fun initChart(): Node {
        type.attackAbilities.forEachIndexed { index, _ -> attackCurves += AttackCurve(type, index) }
        update()

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

    private fun update() {
        hero.updateAttributes()
        priceLabel.text = "总价:\n${hero.price}"
        val passiveHaste = type.passiveHaste
        if (passiveHaste > 0) {
            passiveLabel.isVisible = true
            passiveLabel.text = "${type.passiveHasteName}\n攻速+${passiveHaste / 10}%"
        } else {
            passiveLabel.isVisible = false
        }

        criticalLabels[0].text = hero.criticalDamageRuneBonus.toPercent()
        criticalLabels[1].text = hero.criticalRuneBonus.toPercent()
        if (hero.avgAttack[0] > 0) {
            avgLabels[0].text = "${hero.avgAttack[0]}"
            avgLabels[1].text = "${hero.avgAttack[1]}"
        }
        extraLabel.text = "+${hero.extraAttack}"

        val haste = hero.expectedHaste
        val storm = type.attackAbilities[0].canCritical && 1136 in hero.type.equips
        xAxis.label = "攻速加成 +${haste / 10f}%"
        attackCurves.forEach { it.update(haste, storm, hero.level) }
    }

    private fun initBattle() = Button("攻击吕布").apply {
        padding = Insets(16.0)
        setOnAction { startStage("", BattlePane(hero, Hero("吕布".toHero()!!))) }
    }

    companion object : HashMap<String, HeroPane>() {
        private fun Int.toPercent() = "+%.4f%%".format(this / 10000f)
        private fun GridPane.add(key: String, value: Labeled, row: Int) {
            val label = Label(key)
            label.prefWidth = 60.0
            label.alignment = Pos.TOP_CENTER
            add(label.border(), 0, row)

            value.prefWidth = 70.0
            value.alignment = Pos.TOP_CENTER
            add(value.border(), 1, row)
        }
    }
}