package apc.eagle.fx.battle

import apc.eagle.common.Ability
import apc.eagle.common.Event
import javafx.geometry.Pos
import javafx.scene.control.TableCell
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight

internal class DamageCell : TableCell<Event, Event>() {

    init {
        alignment = Pos.CENTER
    }

    override fun updateItem(item: Event?, empty: Boolean) {
        super.updateItem(item, empty)
        if (item == null) {
            text = ""
        } else {
            text = when (item.type) {
                Event.TYPE_ON -> {
                    font = defaultFont
                    when (item.ability.type) {
                        Ability.TYPE_BUFF -> textFill = Color.MIDNIGHTBLUE
                        Ability.TYPE_DEBUFF -> textFill = Color.ORANGERED
                    }
                    item.ability.tipOn
                }
                Event.TYPE_OFF -> {
                    font = defaultFont
                    textFill = Color.DARKGRAY
                    item.ability.tipOff
                }
                else -> {
                    font = if (item.critical) criticalFont else damageFont
                    when (item.ability.type) {
                        Ability.TYPE_REGEN -> textFill = Color.LIMEGREEN
                        Ability.TYPE_PHYSICAL -> textFill = Color.RED
                        Ability.TYPE_MAGIC -> textFill = Color.DEEPPINK
                        Ability.TYPE_REAL -> textFill = Color.BLACK
                    }
                    item.damage.toString()
                }
            }
        }
    }

    companion object {
        private val criticalFont = Font.font("Impact", FontWeight.SEMI_BOLD, 24.0)
        private val damageFont = Font.font("Verdana", FontWeight.MEDIUM, FontPosture.ITALIC, 16.0)
        private val defaultFont = Font.getDefault()
    }
}