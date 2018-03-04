package almostct.top.foodhack.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
@Parcelize
data class Comment(
        val account: Account,
        val target: String,
        val text: String,
        val date: Long,
        val likes: Int,
        val dislikes: Int
) : Parcelable