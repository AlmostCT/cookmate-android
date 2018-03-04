package almostct.top.foodhack.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */
@Parcelize
data class Account(
        val handle: String,
        val rating: Int,
        val avatar: String?,
        val achievements: List<Achievement>
) : Parcelable