package almostct.top.foodhack.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
@Parcelize
data class Achievement(
        val name: String,
        val description: String,
        val image: String
) : Parcelable