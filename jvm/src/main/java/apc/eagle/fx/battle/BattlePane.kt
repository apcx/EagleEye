package apc.eagle.fx.battle

import apc.common.center
import apc.common.plus
import apc.common.right
import apc.eagle.common.*
import apc.eagle.common.Event.Companion.TYPE_HIT
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.VBox

class BattlePane(type: HeroType) : VBox() {

    private val table = TableView<Event>()

    init {
        val target = Hero("吕布".toHero()!!)
        val battle = Battle(Hero(type), target)
        val button = Button("攻击吕布\nHP : ${target.mhp}")
        button.padding = Insets(16.0)
        button.setOnAction { table.items.setAll(battle.fight()) }

        alignment = Pos.TOP_CENTER
        this + button + initTable()
    }

    private fun initTable() = table.apply {
        val time = TableColumn<Event, Int>("毫秒")
        time.cellValueFactory = PropertyValueFactory<Event, Int>(Event::time.name)
        time.right()

        val hero = TableColumn<Event, Number>("英雄")
        hero.setCellValueFactory {
            val ev = it.value
            val icon = if (ev.type == TYPE_HIT) ev.attacker.type.skins[0].icon else ev.target.type.skins[0].icon
            SimpleIntegerProperty(icon)
        }
        hero.setCellFactory { HeroCell() }

        val ability = TableColumn<Event, String>()
        ability.setCellValueFactory {
            val event = it.value
            var name = event.ability.name
            if (event.type == TYPE_HIT && event.ability == event.attacker.type.attackAbilities[0]) name = "攻击"
            SimpleStringProperty(name)
        }
        ability.center()

        val target = TableColumn<Event, Number>("目标")
        target.setCellValueFactory {
            val event = it.value
            SimpleIntegerProperty(if (event.type == TYPE_HIT) event.target.type.skins[0].icon else 0)
        }
        target.setCellFactory { HeroCell() }

        val damage = TableColumn<Event, Event>()
        damage.setCellValueFactory { SimpleObjectProperty(it.value) }
        damage.setCellFactory { DamageCell() }

        val hp = TableColumn<Event, String>("HP")
        hp.setCellValueFactory { SimpleStringProperty(if (it.value.hp >= 0) it.value.hp.toString() else "") }
        hp.right()

        setPrefSize(600.0, 600.0)
        columnResizePolicy = TableView.UNCONSTRAINED_RESIZE_POLICY
        columns.setAll(time, hero, ability, target, damage, hp)
        items.setAll()
    }
}