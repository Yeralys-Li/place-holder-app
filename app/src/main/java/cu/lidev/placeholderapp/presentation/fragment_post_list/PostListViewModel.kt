package cu.lidev.placeholderapp.presentation.fragment_post_list

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lidev.core.common.wrappers.Resource
import cu.lidev.placeholderapp.data.usercases.PostListUseCase
import cu.lidev.placeholderapp.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PostListUiState<out T> {
    object Loading : PostListUiState<Nothing>()
    data class Error(@StringRes val errorMessage: Int) : PostListUiState<Nothing>()
}

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val useCase: PostListUseCase
) : ViewModel() {

    private val _uiState = Channel<PostListUiState<Nothing>>()
    val uiState: Flow<PostListUiState<Nothing>> = _uiState.receiveAsFlow()

    private val _data: MutableLiveData<List<Post>> by lazy { MutableLiveData() }
    val data: LiveData<List<Post>> = _data

    fun getAll() = viewModelScope.launch {
        _uiState.send(PostListUiState.Loading)
        when (val result = useCase()) {
            is Resource.Success -> {
                _data.postValue(result.data)
            }
            is Resource.Error -> {
                _uiState.send(PostListUiState.Error(result.message ?: -1))
            }
        }
    }


}