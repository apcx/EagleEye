package apc.eagle.season

import apc.common.BindFragment
import apc.eagle.databinding.FragmentSeasonRewardBinding

class SeasonRewardFragment : BindFragment<FragmentSeasonRewardBinding, SeasonRewardModel>() {

    override fun onStart() {
        super.onStart()
        vm.onLoad(activity!!)
        vm.onUpdate()
    }

    override fun onResume() {
        super.onResume()
        vm.resumed = true
    }

    override fun onStop() {
        vm.onSave(activity)
        super.onStop()
    }
}