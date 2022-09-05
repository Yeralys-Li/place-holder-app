package cu.lidev.placeholderapp.presentation.fragment_post_list

import androidx.lifecycle.ViewModel
import cu.lidev.placeholderapp.data.usercases.PostListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(useCase: PostListUseCase) : ViewModel() {


}