package cu.lidev.placeholderapp.data.repository

import cu.lidev.core.common.network.RequestHandler
import cu.lidev.core.common.network.ResponseResult
import cu.lidev.core.common.wrappers.Resource
import cu.lidev.placeholderapp.data.api.ApiService
import cu.lidev.placeholderapp.domain.model.Comment
import cu.lidev.placeholderapp.domain.model.Post
import cu.lidev.placeholderapp.domain.repository.PostRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRepoImpl @Inject constructor(
    private val apiService: ApiService,
    private val requestHandler: RequestHandler
) : PostRepo {

    override suspend fun getAllPost(): Resource<List<Post>> = withContext(Dispatchers.IO) {

        return@withContext when (val response =
            requestHandler.safeApiCall { apiService.getAllPost() }) {
            is ResponseResult.Success -> {
                Resource.Success(response.data ?: listOf())
            }
            is ResponseResult.Error -> {
                Resource.Error(message = response.exception.second)
            }
        }

    }

    override suspend fun getComments(postId: Int): Resource<List<Comment>> =
        withContext(Dispatchers.IO) {

            return@withContext when (val response =
                requestHandler.safeApiCall { apiService.getComments(postId = postId) }) {
                is ResponseResult.Success -> {
                    Resource.Success(response.data ?: listOf())
                }
                is ResponseResult.Error -> {
                    Resource.Error(message = response.exception.second)
                }
            }
        }
}