package almostct.top.foodhack.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface Client {
//    @GET("/posts/{id}")
//    fun getPost(@Path("id") id: Long): Observable<Post>

    @GET("/recognize")
    fun recognize(@Query("recipeId") recipeId: String, @Query("stepId") stepId: Int, @Query("text") text: String): Observable<RecognitionResponse>
}

data class Post(val userId: Long, val id: Long, val title: String, val body: String)

data class RecognitionResponse(val response: String)
