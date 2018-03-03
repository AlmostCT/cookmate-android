package almostct.top.foodhack.ui.common

import activitystarter.ActivityStarter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import icepick.Icepick

abstract class InjectableActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityStarter.fill(this, savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        ActivityStarter.save(this, outState)
        Icepick.saveInstanceState(this, outState);
    }
}
