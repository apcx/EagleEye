package apc.eagle.fx.rune

import apc.common.copyable
import apc.common.onCopy
import apc.common.plus
import apc.eagle.common.HeroType
import apc.eagle.common.Rune
import apc.eagle.common.RuneConfig
import apc.eagle.common.toRune
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox
import javafx.scene.text.Text

internal class ColorRunePane(private val hero: HeroType, private val color: Int) : BorderPane() {

    private val buttons = Array(3) { RuneButton() }

    init {
        when (color) {  // http://www.w3school.com.cn/cssref/css_colornames.asp
            Rune.RED -> style = "-fx-background-color: lavenderBlush"
            Rune.BLUE -> style = "-fx-background-color: lightCyan"
            Rune.GREEN -> style = "-fx-background-color: paleGreen"
        }
        padding = Insets(2.0)
        top = allRunes()
        bottom = initCurrentRunes()
    }

    private fun allRunes(includeAlpha: Boolean = false) = TilePane(2.0, 2.0).apply {
        prefColumns = 3
        if (includeAlpha) {
            Rune.idMap.values.filter { it.color == color && it.level == 5 }.map { it.name }
        } else {
            when (color) {
                Rune.RED -> {
                    prefColumns = 4
                    listOf(
                        "无双", "异变", "宿命", "梦魇",
                        "祸源", "传承", "", "凶兆",
                        "红月", "纷争", "", "圣人"
                    )
                }
                Rune.BLUE -> listOf(
                    "狩猎", "调和", "轮回",
                    "隐匿", "冥想", "贪婪",
                    "夺萃", "长生", "",
                    "繁荣", "兽痕"
                )
                Rune.GREEN -> listOf(
                    "鹰眼", "虚空", "心眼",
                    "", "霸者", "怜悯",
                    "", "灵山", "献祭",
                    "", "均衡", "敬畏",
                    "", "回声"
                )
                else -> listOf()
            }
        }.forEach { this + (if (it.isEmpty()) Text() else Button(it).copyable()) }
    }

    private fun initCurrentRunes() = VBox(2.0, *buttons).apply {
        padding = Insets(4.0, 0.0, 0.0, 0.0)
        alignment = Pos.BOTTOM_CENTER
        buttons.forEachIndexed { index, button ->
            button.onCopy { string, drop ->
                val rune = string.toRune()
                if (rune == null || rune.color != color || buttons.any { it.rune == rune }) {
                    false
                } else {
                    if (drop) button.rune = rune
                    true
                }
            }
            button.setOnAction {
                if (button.rune != null && button.count < 10) {
                    ++button.count
                    for (i in buttons.lastIndex downTo 0) {
                        if (i != index) {
                            val toReduce = buttons[i]
                            if (toReduce.count > 0) {
                                --toReduce.count
                                break
                            }
                        }
                    }
                }
            }
        }

        hero.runeConfig.toRunes(color).forEachIndexed { index, it ->
            buttons[index].count = it.second
            buttons[index].rune = it.first
        }
    }

    internal fun applyConfig(config: RuneConfig) {
        buttons.forEach {
            it.rune = null
            it.count = 0
        }
        config.toRunes(color).forEachIndexed { index, it ->
            buttons[index].count = it.second
            buttons[index].rune = it.first
        }
    }

    internal fun save() {
        val runes = hero.runeConfig.ids[color - 1]
        runes.clear()
        buttons.forEach {
            val rune = it.rune
            if (rune != null && it.count > 0) runes[rune.id] = it.count
        }
    }
}