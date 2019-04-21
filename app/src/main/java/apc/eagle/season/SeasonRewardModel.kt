package apc.eagle.season

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import apc.common.PairAdapter
import apc.common.update
import apc.eagle.R
import java.util.*
import kotlin.math.max

private const val SEASON_LAST_WEEK = 25
private const val SEASON_LAST_DAY = 174
private const val BASE_WEEK = 16
private const val BASE_WEEK_EXP = 16800

class SeasonRewardModel : ViewModel() {

    val weeks = MutableLiveData<Int>()
    val level = MutableLiveData<Int>()
    val exp = MutableLiveData<Int>()
    val challenges = MutableLiveData<Int>()
    val login = MutableLiveData<Int>()
    val weekExp = MutableLiveData<Int>()
    val weekExpMax = MutableLiveData<Int>()
    val paid = MutableLiveData<Boolean>()
    val epic = MutableLiveData<Boolean>()

    val result = MediatorLiveData<String>()
    val adapter = PairAdapter<CharSequence, CharSequence>(R.layout.item_season)

    internal var resumed = false
    private val calendar = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY }
    private var lastUpdateDay = 0
    private var lastUpdateWeek = 0

    init {
        result.addSource(level) { if (resumed) onUpdate() }
        result.addSource(exp) { if (resumed) onUpdate() }
        result.addSource(paid) { if (resumed) onUpdate() }
        result.addSource(epic) { if (resumed) onUpdate() }
        result.addSource(challenges) { if (resumed) onUpdate() }
        result.addSource(login) { if (resumed) onUpdate() }
        result.addSource(weekExp) { if (resumed) onUpdate() }
    }

    internal fun onUpdate() {
        lastUpdateWeek = calendar[Calendar.WEEK_OF_YEAR]
        var requiredExp = 2000 * (80 - level.value!!) - exp.value!!
        val weekMax = BASE_WEEK_EXP + 830 * (lastUpdateWeek - BASE_WEEK)
        weekExpMax.update(weekMax)

        val items = mutableListOf<Pair<CharSequence, CharSequence>>()
        val isPaid = paid.value!!
        val wantEpic = epic.value!!
        if (!isPaid && wantEpic) {
            items += "典藏进阶卡" to "2000 x 30"
            requiredExp -= 2000 * 30
        }
        val remainWeeks = SEASON_LAST_WEEK - lastUpdateWeek
        weeks.update(1 + remainWeeks)
        if (requiredExp > 0 && remainWeeks > 0) {
            items += "每周签到经验包" to "3000 x $remainWeeks （假设本周已领取）"
            requiredExp -= 3000 * remainWeeks
        }
        lastUpdateDay = calendar[Calendar.DAY_OF_YEAR]
        if (requiredExp > 0) {
            val achievedChallenges = challenges.value!!
            if (achievedChallenges < 5) {
                var remainChallenges = 5 - achievedChallenges
                if (30 - login.value!! > max(0, SEASON_LAST_DAY - lastUpdateDay)) {
                    items += "累积登录 30 天" to "天数不够，无法达成"
                    --remainChallenges
                }
//                if (remainChallenges > 0) {
//                    val exp = 3500 * remainChallenges
//                    items += "赛季挑战" to "3500 x $remainChallenges"
//                    requiredExp -= exp
//                }
            }
        }
        if (requiredExp > 0) {
            val achievedExp = weekExp.value!!
            if (achievedExp < weekMax) {
                val remainExp = weekMax - achievedExp
                items += "本周任务经验" to remainExp.toString()
                requiredExp -= remainExp
            }
        }
        if (requiredExp > 0) {
            val questExp = mutableListOf<Int>()
            repeat(remainWeeks) {
                if (requiredExp > 0) {
                    val exp = weekMax + 830 * (1 + it)
                    questExp += exp
                    requiredExp -= exp
                }
            }
            items += "每周任务经验" to questExp.joinToString(" + ")
        }
        var text: String
        if (requiredExp > 0) {
            items += "距离 80 级还缺" to requiredExp.toString()
            var requiredLevel = requiredExp / 2000
            if (requiredExp % 2000 != 0) ++requiredLevel
            val currency = 80 * requiredLevel
            text = "需额外花费 $currency 点券，用于购买等级"
            if (!isPaid) {
                text = "除进阶卡以外，还$text"
                if (currency >= 1288 - 388) text += "\n因点券缺口较大，建议购买【典藏进阶卡】"
            }
        } else {
            text = "估计可达 80 级"
            if (!isPaid) text += "，可购买${if (wantEpic) "典藏进阶卡" else "进阶卡"}"
        }
        result.update(text)
        adapter.submitList(items)
    }

    internal fun onLoad(activity: Activity) {
        val today = calendar[Calendar.DAY_OF_YEAR]
        val week = calendar[Calendar.WEEK_OF_YEAR]

        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        weeks.update(SEASON_LAST_WEEK - week + 1)
        level.update(sp.getInt("s15_level", 1))
        exp.update(sp.getInt("s15_exp", 0))
        challenges.update(sp.getInt("s15_challenges", 0))
        var loginDays = sp.getInt("s15_login", 1)
        paid.update(sp.getBoolean("s15_paid", false))
        epic.update(sp.getBoolean("s15_epic", false))
        lastUpdateDay = sp.getInt("s15_lastUpdateDay", today)
        lastUpdateWeek = sp.getInt("s15_lastUpdateWeek", week)

        if (today > lastUpdateDay) ++loginDays
        login.update(loginDays)
        weekExp.update(if (week > lastUpdateWeek) 0 else sp.getInt("s15_weekExp", 0))
    }

    internal fun onSave(activity: Activity?) {
        activity?.getPreferences(Context.MODE_PRIVATE)?.edit {
            putInt("s15_level", level.value!!)
            putInt("s15_exp", exp.value!!)
            putInt("s15_challenges", challenges.value!!)
            putInt("s15_login", login.value!!)
            putInt("s15_weekExp", weekExp.value!!)
            putBoolean("s15_paid", paid.value!!)
            putBoolean("s15_epic", epic.value!!)
            putInt("s15_lastUpdateDay", lastUpdateDay)
            putInt("s15_lastUpdateWeek", lastUpdateWeek)
        }
    }
}