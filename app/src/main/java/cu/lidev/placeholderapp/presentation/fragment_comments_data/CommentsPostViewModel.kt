package cu.lidev.placeholderapp.presentation.fragment_comments_data

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lidev.core.common.wrappers.Resource
import cu.lidev.placeholderapp.data.usercases.GetCommentsUseCase
import cu.lidev.placeholderapp.domain.model.Comment
import cu.lidev.placeholderapp.presentation.fragment_post_list.PostListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CommentsPostUiState<out T> {
    object Loading : CommentsPostUiState<Nothing>()
    data class Error(@StringRes val errorMessage: Int) : CommentsPostUiState<Nothing>()
}

@HiltViewModel
class CommentsPostViewModel @Inject constructor(
    private val useCase: GetCommentsUseCase
) : ViewModel() {


    private val _uiState = Channel<PostListUiState<Nothing>>()
    val uiState: Flow<PostListUiState<Nothing>> = _uiState.receiveAsFlow()

    private val _data: MutableLiveData<List<Comment>> by lazy { MutableLiveData() }
    val data: LiveData<List<Comment>> = _data

    fun getComments(postId: Int) = viewModelScope.launch {
        _uiState.send(PostListUiState.Loading)
        when (val result = useCase(postId = postId)) {
            is Resource.Success -> {
                _data.postValue(result.data)
            }
            is Resource.Error -> {
                _uiState.send(PostListUiState.Error(result.message ?: -1))
            }
        }
    }

}