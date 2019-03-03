package apc.eagle.fx

import apc.common.IntSlider
import apc.common.onCopy
import apc.common.plus
import apc.common.setCopyable
import apc.eagle.common.HeroType
import apc.eagle.common.Rune
import apc.eagle.common.toRune
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox
import javafx.scene.text.Text

class ColorRunePane(private val hero: HeroType, private val color: Int) : BorderPane() {

    val button = Button()
    val runes = mutableMapOf<Rune, Int>()

    init {
        hero.neoRunes[color - 1].forEach { id, count -> id.toRune()?.let { runes[it] = count } }

        button.onCopy { string, drop ->
            val rune =
                try {
                    Rune.idMap[string.toInt()]
                } catch (e: NumberFormatException) {
                    Rune.nameMap[string]
                }
            if (rune == null || rune.color != color) {
                false
            } else {
                if (drop) rune copyTo button
                true
            }
        }

        val bottomPane = VBox(2.0).apply { alignment = Pos.CENTER } + button

        when (color) {  // http://www.w3school.com.cn/cssref/css_colornames.asp
            Rune.RED -> style = "-fx-background-color: lavenderBlush"
            Rune.BLUE -> style = "-fx-background-color: lightCyan"
            Rune.GREEN -> style = "-fx-background-color: paleGreen"
        }
        padding = Insets(2.0)
        top = initAllRunes()
        bottom = bottomPane
    }

    private fun initAllRunes(): Node {
        val pane = TilePane(2.0, 2.0)
        pane.prefColumns = 3
        when (color) {
            Rune.RED -> {
                pane.prefColumns = 4
                arrayOf(
                    "无双", "异变", "宿命", "梦魇",
                    "祸源", "传承", "", "凶兆",
                    "红月", "纷争", "", "圣人"
                )
            }
            Rune.BLUE -> arrayOf(
                "狩猎", "调和", "轮回",
                "隐匿", "冥想", "贪婪",
                "夺萃", "长生", "",
                "繁荣", "", "",
                "兽痕"
            )
            Rune.GREEN -> arrayOf(
                "鹰眼", "虚空", "心眼",
                "", "霸者", "怜悯",
                "", "灵山", "献祭",
                "", "均衡", "敬畏",
                "", "回声"
            )
            else -> arrayOf("")
        }.forEach {
            val rune = it.toRune()
            if (rune == null) {
                pane + Text()
            } else {
                val button = Button(rune.name).setCopyable()
                button.userData = rune.id
                pane + button
            }
        }
        return pane
    }

    private fun initSlider(): Node {
        val map = hero.neoRunes[color - 1].toList()
        val buttons = Array(2) {
            Button().apply {
            }
        }
        var index = 0
        runes.forEach { rune, count ->
            val button = buttons[index++]
        }

        val button1 = Button()
        val button2 = Button()
        val vBox = VBox(2.0).apply { alignment = Pos.CENTER } + button1 + button2
        val slider = IntSlider(0, 10) {

        }
        slider.orientation = Orientation.VERTICAL
        slider.prefHeight = 180.0

        val pane = BorderPane()
        pane.right = slider
        pane.center = vBox
        return pane
    }
}

private class RuneButton() : Button() {
    init {
        graphic = ImageView()
        onCopy { string, drop ->
            false
        }
    }

    fun update(count: Int) {
        val imageView = graphic as ImageView
        if (count <= 0) {
            imageView.image = null
            text = "无"
        } else {
            val rune = id.toRune()
            imageView.image
        }
    }
}