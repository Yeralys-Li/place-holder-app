package cu.lidev.placeholderapp.data.api

import cu.lidev.placeholderapp.domain.model.Comment
import cu.lidev.placeholderapp.domain.model.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    /**
     * Get all post
     */
    @GET("posts")
    suspend fun getAllPost(
    ): Response<List<Post>>

    /**
     * Get comments of the post
     */
    @GET("comments")
    suspend fun getComments(
        @Query("postId") postId: Int,
    ): Response<List<Comment>>


}