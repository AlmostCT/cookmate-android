package almostct.top.foodhack.ui.feed

import almostct.top.foodhack.App
import almostct.top.foodhack.MainActivity
import almostct.top.foodhack.R
import almostct.top.foodhack.api.Client
import almostct.top.foodhack.model.FeedNewsElement
import almostct.top.foodhack.model.FeedRecipeElement
import almostct.top.foodhack.model.Recipe
import almostct.top.foodhack.ui.common.defaultSub
import almostct.top.foodhack.ui.recipe.RecipeActivityStarter
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import kotterknife.bindView
import java.util.*

class FeedFragment() : Fragment() {
    companion object {
        operator fun invoke() = FeedFragment()
    }

    private lateinit var cli: Client


    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        cli = ((activity as? MainActivity)?.application as? App)?.client ?: throw AssertionError("Not that activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    val recipesView by bindView<RecyclerView>(R.id.feed_recipes)
    val newsView by bindView<RecyclerView>(R.id.feed_news)
    val featuredViews by bindView<RecyclerView>(R.id.feed_featured)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchUI()
    }

    private fun fetchUI() {
        cli.getUsersRecipes(3, 3).defaultSub(this@FeedFragment::updateUI)
    }

    private fun updateUI(data: List<Recipe>) {
        recipesView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newsView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recipesView.adapter = RotmAdapter(data.subList(0, 3))
        newsView.adapter = NewsAdapter(
            listOf(
                FeedNewsElement("Рецепт месяца", "Голосуйте за лучший рецепт месяца!"),
                FeedNewsElement("Скидки", "Скидки до 30% в Пртии Еды")
            )
        )
        featuredViews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        featuredViews.adapter = RotmAdapter(data.subList(3, 6))
    }

    inner class RotmAdapter(private val horizontalList: List<FeedRecipeElement>) :
        RecyclerView.Adapter<RotmAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView by bindView<TextView>(R.id.feed_item_text)
            val imageView by bindView<ImageView>(R.id.feed_item_image)
            val cardView by bindView<CardView>(R.id.feed_item_card)
            val ratingBar by bindView<RatingBar>(R.id.feed_recipe_rating)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_recipe_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = horizontalList[position].name
            val img = rollMock(position)
            holder.imageView.setImageResource(img)
            holder.ratingBar.rating = horizontalList[position].rating.toFloat()
            holder.cardView.setOnClickListener {
                RecipeActivityStarter.start(this@FeedFragment.context, horizontalList[position])
            }
        }

        override fun getItemCount() = horizontalList.size
    }

    inner class NewsAdapter(private val horizontalList: List<FeedNewsElement>) :
        RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView by bindView<TextView>(R.id.feed_item_text)
            val imageView by bindView<ImageView>(R.id.feed_item_image)
            val bodyText by bindView<TextView>(R.id.feed_news_item_content)
            val cardView by bindView<CardView>(R.id.feed_item_card)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_news_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = horizontalList[position].title
            holder.bodyText.text = horizontalList[position].body
            val img = rollMock(position)
            holder.imageView.setImageResource(img)

            holder.cardView.setOnClickListener {
                Toast.makeText(
                    this@FeedFragment.context,
                    holder.textView.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun getItemCount() = horizontalList.size
    }
}

private val rand = Random()

fun rollMock(position: Int) = when (rand.nextInt(6)) {
    0 -> R.drawable.mock_food_1
    1 -> R.drawable.mock_food_2
    2 -> R.drawable.mock_food_3
    3 -> R.drawable.mock_food_4
    4 -> R.drawable.mock_food_5
    5 -> R.drawable.mock_food_6
    else -> throw AssertionError("Math fucked")
}
