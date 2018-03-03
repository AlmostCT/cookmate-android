package almostct.top.foodhack.ui.feed

import almostct.top.foodhack.R
import almostct.top.foodhack.model.FeedNewsElement
import almostct.top.foodhack.model.FeedRecipeElement
import almostct.top.foodhack.ui.recipe.RecipeActivityStarter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotterknife.bindView

class FeedFragment private constructor() : Fragment() {
    companion object {
        operator fun invoke() = FeedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    val recipesView by bindView<RecyclerView>(R.id.feed_recipes)
    val newsView by bindView<RecyclerView>(R.id.feed_news)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recipesView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newsView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recipesView.adapter = RotmAdapter(listOf("Recipe one", "Recipe two", "Recipe three"))
        newsView.adapter = NewsAdapter(
            listOf(
                FeedNewsElement("News 1", "wow such much"),
                FeedNewsElement("News 2", "/shrug")
            )
        )
    }

    inner class RotmAdapter(private val horizontalList: List<FeedRecipeElement>) :
        RecyclerView.Adapter<RotmAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView by bindView<TextView>(R.id.feed_item_text)
            val imageView by bindView<ImageView>(R.id.feed_item_image)
            val cardView by bindView<CardView>(R.id.feed_item_card)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_recipe_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.textView.text = horizontalList[position]
            val img = rollMock(position)
            holder.imageView.setImageResource(img)

            holder.cardView.setOnClickListener {
                RecipeActivityStarter.start(this@FeedFragment.context, position.toString(), horizontalList[position])
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

fun rollMock(position: Int) = when (position % 3) {
    0 -> R.drawable.mock_food_1
    1 -> R.drawable.mock_food_2
    2 -> R.drawable.mock_food_3
    else -> throw AssertionError("Math fucked")
}
