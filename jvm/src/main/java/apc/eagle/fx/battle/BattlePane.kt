package apc.eagle.fx.battle

import apc.common.center
import apc.common.plus
import apc.common.right
import apc.eagle.common.Battle
import apc.eagle.common.Event
import apc.eagle.common.Event.Companion.TYPE_HIT
import apc.eagle.common.Hero
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox

class BattlePane(private val attacker: Hero, private val defender: Hero) : VBox(4.0) {

    private val battle = Battle(attacker, defender)
    private val mhp = Array(2) { Label() }
    private val price = Array(2) { Label() }
    private val damageLabels = Array(2) { Label() }
    private val dpsLabels = Array(2) { Label() }
    private val rankLabels = Array(2) { Label() }
    private val table = TableView<Event>()

    init {
        padding = Insets(2.0)
        alignment = Pos.TOP_CENTER
        updateAttributes()
        this + initGrid() + initButton() + initTable()
    }

    private fun initGrid() = GridPane().apply {
        alignment = Pos.TOP_CENTER
        isGridLinesVisible = true
        add(0, "", attacker.type.name, defender.type.name)
        add(1, "HP", mhp)
        add(2, "装备总价", price)
        add(3, "总伤害", damageLabels)
        add(4, "每秒伤害", dpsLabels)
        add(5, "性价比", rankLabels)
    }

    private fun initButton() = Button("开战").apply {
        padding = Insets(8.0, 50.0, 8.0, 50.0)
        setOnAction {
            battle.fight()
            updateAttributes()
            val time = battle.logs.last().time
            battle.forces.flatten().forEachIndexed { index, hero ->
                val damage = hero.totalDamage
                val dps = hero.totalDamage * 1000f / time
                damageLabels[index].text = damage.toString()
                dpsLabels[index].text = "%.1f".format(dps)
                rankLabels[index].text = "%.4f".format(dps * 10 / (hero.price + 1))
            }
            table.items.setAll(battle.logs)
        }
    }

    private fun updateAttributes() {
        mhp[0].text = attacker.mhp.toString()
        mhp[1].text = defender.mhp.toString()
        price[0].text = attacker.price.toString()
        price[1].text = defender.price.toString()
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
    }

    companion object : HashMap<String, BattlePane>() {

        fun GridPane.add(row: Int, key: String, vararg hero: String) {
            add(row, key, hero.map(::Label).toTypedArray())
        }

        fun GridPane.add(row: Int, key: String, heroes: Array<out Labeled>) {
            val label = Label(key)
            label.prefWidth = 60.0
            label.padding = Insets(2.0)
            label.alignment = Pos.TOP_CENTER
            add(label, 0, row)
            heroes.forEachIndexed { index, it ->
                it.prefWidth = 60.0
                it.padding = Insets(2.0)
                it.alignment = Pos.TOP_CENTER
                add(it, 1 + index, row)
            }
        }
    }
}