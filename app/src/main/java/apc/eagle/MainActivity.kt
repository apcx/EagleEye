package apc.eagle

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import apc.common.startFragment
import apc.eagle.season.SeasonRewardFragment

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View) {
        startFragment<SeasonRewardFragment>()
    }
}