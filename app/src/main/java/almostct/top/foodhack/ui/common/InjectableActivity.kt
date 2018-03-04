package almostct.top.foodhack.ui.common

import activitystarter.ActivityStarter
import almostct.top.foodhack.App
import almostct.top.foodhack.R
import almostct.top.foodhack.api.Client
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import icepick.Icepick

abstract class InjectableActivity : AppCompatActivity() {

    protected fun getClient(): Client = (application as App).client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityStarter.fill(this, savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        getActionBar()?.setTitle(Html.fromHtml("<font color=\"black\">" + getActivityTitle() + "</font>"));
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"black\">" + getActivityTitle() + "</font>"));
    }

    override fun onResume() {
        super.onResume()
        getActionBar()?.setTitle(Html.fromHtml("<font color=\"black\">" + getActivityTitle() + "</font>"));
        supportActionBar?.setTitle(Html.fromHtml("<font color=\"black\">" + getActivityTitle() + "</font>"));
    }

    open fun getActivityTitle(): String = getString(R.string.app_name)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        ActivityStarter.save(this, outState)
        Icepick.saveInstanceState(this, outState);
    }
}
