package cu.lidev.placeholderapp.domain.repository

import cu.lidev.core.common.wrappers.Resource
import cu.lidev.placeholderapp.domain.model.Comment
import cu.lidev.placeholderapp.domain.model.Post

interface PostRepo {

    suspend fun getAllPost(): Resource<List<Post>>

    suspend fun getComments(postId: Int): Resource<List<Comment>>

}