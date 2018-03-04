package almostct.top.foodhack.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author  Vadim Semenov (semenov@rain.ifmo.ru)
 */

@Parcelize
data class Recipe(
        val timeCreation: Long,
        val userName: String? = null,
        val name: String,
        val rating: Int,
        val tools: String,
        val weight: String,
        val proteins: String,
        val fats: String,
        val carbohydrates: String,
        val calories: String,
        val totalTime: String,
        val picture: String?,
        val ingredients: List<Product>,
        val steps: List<RecipeStep>,
        val recipeId: String
) : Parcelable

@Parcelize
data class RecipeStep(
        val stepId: Int,
        val shortDescription: String,
        val longDescription: String,
        val time: Long,
        val products: List<Product>
) : Parcelable

@Parcelize
data class Product (
        val name: String,
        val amount: String
) : Parcelable
