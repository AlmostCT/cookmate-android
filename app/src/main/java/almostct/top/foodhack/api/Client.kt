package almostct.top.foodhack.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface Client {
    @GET("/posts/{id}")
    fun getPost(@Path("id") id: Long): Observable<Post>
}

data class Post(val userId: Long, val id: Long, val title: String, val body: String)
