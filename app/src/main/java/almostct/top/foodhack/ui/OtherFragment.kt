package almostct.top.foodhack.ui

import almostct.top.foodhack.R
import almostct.top.foodhack.model.Achievement
import almostct.top.foodhack.model.DummyData
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.other_fragment.*
import kotterknife.bindView


class OtherFragment() : Fragment() {
    companion object {
        operator fun invoke() = OtherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.other_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profile_achievements.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        profile_achievements.adapter = AchievementsAdapter(
            listOf(
                DummyData.firstLoveAchievement,
                DummyData.pancakeAchievement
            )
        )
    }

    inner class AchievementsAdapter(private val horizontalList: List<Achievement>) :
        RecyclerView.Adapter<AchievementsAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            //            val textView by bindView<TextView>(R.id.achievement_item_text)
            val imageView by bindView<ImageView>(R.id.achievement_item_image)
//            val cardView by bindView<CardView>(R.id.feed_item_card)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.achievement_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//            holder.textView.text = horizontalList[position].description
            val img = rollAchieve(horizontalList[position].image)
            holder.imageView.setImageResource(img)
        }

        override fun getItemCount() = horizontalList.size
    }
}

private fun rollAchieve(image: String) = when (image) {
    "first_love_achievement.png" -> R.drawable.first_love_achievement
    "pancake_achievement.png" -> R.drawable.pancake_achievement
    "plus_one_achievement.png" -> R.drawable.plus_one_achievement
    else -> R.drawable.plus_one_achievement
}
