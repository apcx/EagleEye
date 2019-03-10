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

private const val SEASON_LAST_WEEK = 15
private const val SEASON_LAST_DAY = 97
private const val BASE_WEEK = 11
private const val BASE_WEEK_EXP = 18500

class SeasonRewardModel : ViewModel() {

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
    private var lastUpdateDay = 0
    private var lastUpdateWeek = 0

    init {
        level.value = 1
        exp.value = 0
        challenges.value = 0
        login.value = 1
        weekExp.value = 0
        weekExpMax.value = BASE_WEEK_EXP
        paid.value = false
        epic.value = false

        result.addSource(level) { if (resumed) onUpdate() }
        result.addSource(exp) { if (resumed) onUpdate() }
        result.addSource(paid) { if (resumed) onUpdate() }
        result.addSource(epic) { if (resumed) onUpdate() }
        result.addSource(challenges) { if (resumed) onUpdate() }
        result.addSource(login) { if (resumed) onUpdate() }
        result.addSource(weekExp) { if (resumed) onUpdate() }
    }

    internal fun onUpdate() {
        val calendar = Calendar.getInstance()
        lastUpdateWeek = calendar[Calendar.WEEK_OF_YEAR]
        val weekMax = BASE_WEEK_EXP + 700 * (lastUpdateWeek - BASE_WEEK)
        var requiredExp = 2000 * (80 - level.value!!) - exp.value!!
        weekExpMax.value = weekMax

        val items = mutableListOf<Pair<CharSequence, CharSequence>>()
        val isPaid = paid.value!!
        val wantEpic = epic.value!!
        if (!isPaid && wantEpic) {
            items += "典藏进阶卡" to "2000 x 30"
            requiredExp -= 2000 * 30
        }
        val remainWeeks = SEASON_LAST_WEEK - lastUpdateWeek
        if (requiredExp > 0) {
            if (isPaid) {
                items += "每周签到经验包" to "2000 x $remainWeeks （本周已领取）"
                requiredExp -= 2000 * remainWeeks
            } else {
                items += "每周签到经验包" to "2000 x ${1 + remainWeeks}"
                requiredExp -= 2000 * (1 + remainWeeks)
            }
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
                if (remainChallenges > 0) {
                    val exp = 3500 * remainChallenges
                    items += "赛季挑战" to "3500 x $remainChallenges"
                    requiredExp -= exp
                }
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
                    val exp = weekMax + 700 * (1 + it)
                    questExp += exp
                    requiredExp -= exp
                }
            }
            items += "每周任务经验" to questExp.joinToString(" + ")
        }
        if (requiredExp > 0) {
            items += "距离 80 级还缺" to requiredExp.toString()
            var requiredLevel = requiredExp / 2000
            if (requiredExp % 2000 != 0) ++requiredLevel
            val currency = 80 * requiredLevel
            var text = "需额外花费 $currency 点券，用于购买等级"
            if (!isPaid) {
                text = "除进阶卡以外，还$text"
                if (currency >= 1288 - 388) text += "\n因点券缺口较大，建议购买【典藏进阶卡】"
            }
            result.value = text
        } else {
            var text = "估计可达 80 级"
            if (!isPaid) text += "，可购买${if (wantEpic) "典藏进阶卡" else "进阶卡"}"
            result.value = text
        }
        adapter.submitList(items)
    }

    internal fun onLoad(activity: Activity) {
        val calendar = Calendar.getInstance()
        val sp = activity.getPreferences(Context.MODE_PRIVATE)
        level.update(sp.getInt("s14_level", 1))
        exp.update(sp.getInt("s14_exp", 0))
        challenges.update(sp.getInt("s14_challenges", 0))
        var loginDays = sp.getInt("s14_login", 1)
        paid.update(sp.getBoolean("s14_paid", false))
        epic.update(sp.getBoolean("s14_epic", false))
        lastUpdateDay = sp.getInt("s14_lastUpdateDay", calendar[Calendar.DAY_OF_YEAR])
        lastUpdateWeek = sp.getInt("s14_lastUpdateWeek", calendar[Calendar.WEEK_OF_YEAR])

        val today = calendar[Calendar.DAY_OF_YEAR]
        val week = calendar[Calendar.WEEK_OF_YEAR]
        if (today > lastUpdateDay) ++loginDays
        login.update(loginDays)
        weekExp.update(if (week > lastUpdateWeek) 0 else sp.getInt("s14_weekExp", 0))
    }

    internal fun onSave(activity: Activity?) {
        activity?.getPreferences(Context.MODE_PRIVATE)?.edit {
            putInt("s14_level", level.value!!)
            putInt("s14_exp", exp.value!!)
            putInt("s14_challenges", challenges.value!!)
            putInt("s14_login", login.value!!)
            putInt("s14_weekExp", weekExp.value!!)
            putBoolean("s14_paid", paid.value!!)
            putBoolean("s14_epic", epic.value!!)
            putInt("s14_lastUpdateDay", lastUpdateDay)
            putInt("s14_lastUpdateWeek", lastUpdateWeek)
        }
    }
}