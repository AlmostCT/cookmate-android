package almostct.top.foodhack.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

typealias FeedRecipeElement = String

data class FeedNewsElement(val title: String, val body: String)

@Parcelize
data class Receipt(
    val name: String,
    val rating: Int,
    val tools: String,
    val weight: String,
    val proteins: String,
    val fats: String,
    val carbohydrates: String,
    val calories: String,
    val totalTime: Long,
    val picture: String?,
    val steps: List<ReceiptStep>,
    val id: String? = null
) : Parcelable

@Parcelize
data class ReceiptStep(
    val stepId: Int,
    val shortDescription: String,
    val longDescription: String,
    val time: Long, // seconds
    val products: List<Product>,
    val id: String? = null
) : Parcelable

@Parcelize
data class Product(
    val name: String,
    val amount: String,
    val id: String? = null
) : Parcelable

data class Comment(
    val account: Account,
    val target: String,
    val text: String,
    val date: Long,
    val likes: Int,
    val dislikes: Int,
    val commentId: String? = null
)

data class Account(
    val handle: String,
    val rating: Int,
    val avatar: String?,
//    @DBRef val achievements: List<Achievement>
    val id: String? = null
)
