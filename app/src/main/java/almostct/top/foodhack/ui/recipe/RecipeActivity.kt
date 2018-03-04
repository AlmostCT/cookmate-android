package almostct.top.foodhack.ui.recipe

import activitystarter.Arg
import almostct.top.foodhack.R
import almostct.top.foodhack.model.DummyData
import almostct.top.foodhack.model.Recipe
import almostct.top.foodhack.ui.comments.CommentsActivity
import almostct.top.foodhack.ui.common.InjectableActivity
import almostct.top.foodhack.ui.cooking.CookingActivity2Starter
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.Toolbar
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.marcinmoskala.activitystarter.argExtra
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_recipe.*
import kotterknife.bindView

class RecipeActivity() : InjectableActivity() {
    @get:Arg
    var recipeId: String by argExtra()
    @get:Arg
    var recipeName: String by argExtra()

    val caloriesText by bindView<TextView>(R.id.recipe_energy_info_calories)
    val uText by bindView<TextView>(R.id.recipe_energy_info_u)
    val jText by bindView<TextView>(R.id.recipe_energy_info_j)
    val bText by bindView<TextView>(R.id.recipe_energy_info_b)

    val generalText by bindView<TextView>(R.id.recipe_general_info)
    val descriptionView by bindView<TextView>(R.id.recipe_description)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            CookingActivity2Starter.start(this, DummyData.pancake)
        }
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fetchUI(recipeId)
        recipe_open_comments.setOnClickListener { startActivity(Intent(this, CommentsActivity::class.java)) }
    }

    override fun getActivityTitle(): String {
        return recipeName
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

    fun fetchUI(receiptId: String) {
        Observable.just(DummyData.pancake)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ updateUI(it) }, { Log.e("RECEIPT", "fetch succed", it) })
    }

    fun updateUI(receipt: Recipe) {
        val txt = TextUtils.concat(
            resources.getText(R.string.recipe_general_info_cooktime),
            "2 hours",
            "\n",
            resources.getText(R.string.recipe_general_info_weight),
            receipt.weight
        )
        generalText.text = txt
        caloriesText.text = receipt.calories
        bText.text = receipt.proteins
        jText.text = receipt.fats
        uText.text = receipt.carbohydrates
        descriptionView.text = receipt.tools
        recipe_steps.text = receipt.steps.joinToString(separator = "\n") { it.shortDescription }
        val s1 = Html.fromHtml("<b>User1</b> wow")
        val s2 = Html.fromHtml("<b>User2</b> foo")
        recipe_comments.text = TextUtils.concat(s1, "\n", s2)
    }
}
