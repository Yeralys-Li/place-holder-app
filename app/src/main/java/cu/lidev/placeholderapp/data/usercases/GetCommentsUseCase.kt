package cu.lidev.placeholderapp.data.usercases

import cu.lidev.placeholderapp.domain.repository.PostRepo
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val repo: PostRepo
) {

    suspend operator fun invoke(postId: Int) = repo.getComments(postId = postId)
}