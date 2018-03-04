package almostct.top.foodhack.api

import almostct.top.foodhack.model.Account
import almostct.top.foodhack.model.Comment
import almostct.top.foodhack.model.Recipe
import io.reactivex.Observable
import retrofit2.http.*

interface Client {
    @GET("/accounts/{handle}")
    fun getAccount(@Path("handle") handle: String): Observable<Account?>

    @POST("/saveAccount")
    fun saveAccount(@Body account: Account): Observable<String>


    @GET("/comments/all")
    fun getAllComments(@Query("target") target: String, @Query("step") stepId: Int = -1): Observable<List<Comment>>

    @GET("/comments/top")
    fun getTopComments(@Query("target") target: String, @Query("step") stepId: Int = -1): Observable<List<Comment>>

    @GET("/comments/top ")
    fun getTopComments(@Query("target") target: String, @Query("qty") qty: Int, @Query("step") stepId: Int = -1): Observable<List<Comment>>

    @POST("/postComment")
    fun postComment(@Body comment: Comment): Observable<String>


    @GET("/usersRecipes")
    fun getUsersRecipes(@Query("topCount") topCount: Int, @Query("randomCount") randomCount: Int): Observable<List<Recipe>>


    @GET("/recognize")
    fun recognize(@Query("recipeId") recipeId: String, @Query("stepId") stepId: Int, @Query("text") text: String): Observable<RecognitionResponse>
}

data class RecognitionResponse(val response: String)
