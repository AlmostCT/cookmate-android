package almostct.top.foodhack.ui.recipe

import activitystarter.ActivityStarter
import activitystarter.Arg
import almostct.top.foodhack.R
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.marcinmoskala.activitystarter.argExtra
import kotterknife.bindView


class RecipeActivity internal constructor() : AppCompatActivity() {
    @get:Arg
    var recipeId: String by argExtra()
    @get:Arg
    var recipeName: String by argExtra()

    val caloriesText by bindView<TextView>(R.id.recipe_energy_info_calories)
    val uText by bindView<TextView>(R.id.recipe_energy_info_u)
    val jText by bindView<TextView>(R.id.recipe_energy_info_j)
    val bText by bindView<TextView>(R.id.recipe_energy_info_b)

    val generalText by bindView<TextView>(R.id.recipe_general_info)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityStarter.fill(this, savedInstanceState);
        setContentView(R.layout.activity_recipe)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = recipeName
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Start", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.share, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = if (item?.itemId == R.id.share) {
        Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
        true
    } else false

    fun updateUI() {
        val txt = TextUtils.concat(
            resources.getText(R.string.recipe_general_info_cooktime),
            "2 hours",
            "\n",
            resources.getText(R.string.recipe_general_info_weight),
            "400g"
        )
        generalText.text = txt
        caloriesText.text = "400"
        bText.text = "10"
        jText.text = "20"
        uText.text = "50"
    }

    override// This is optional, only when we want to keep arguments changes in case of rotation etc.
    fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        ActivityStarter.save(this, outState)
    }
}
