package cu.lidev.placeholderapp.presentation.fragment_post_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import cu.lidev.core.common.util.OnItemClickListener
import cu.lidev.core.common.util.RecyclerDecoration
import cu.lidev.placeholderapp.R
import cu.lidev.placeholderapp.databinding.FragmentPostListBinding
import cu.lidev.placeholderapp.domain.model.Post
import cu.lidev.placeholderapp.presentation.fragment_post_list.components.PostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostListFragment : Fragment(), OnItemClickListener<Post> {

    private lateinit var _binding: FragmentPostListBinding
    private val binding get() = _binding
    private val viewModel: PostListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        initUI()
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        viewModel.data.observe(viewLifecycleOwner) {
            refreshing(false)
            (binding.recycler.adapter as PostAdapter).setItems(it)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    when (it) {
                        is PostListUiState.Loading -> {
                            refreshing(true)
                        }
                        is PostListUiState.Error -> {
                            refreshing(false)
                            Snackbar.make(
                                binding.root,
                                getString(it.errorMessage),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
        }
    }

    private fun initUI() {
        binding.apply {
            refresh.setOnRefreshListener { refreshData() }
            recycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                addItemDecoration(
                    RecyclerDecoration(
                        margin = 0.9f,
                        columns = 1
                    )
                )
                adapter = PostAdapter(itemClickListener = this@PostListFragment)
            }
            toolbar.setOnMenuItemClickListener {
                if (it.itemId == R.id.contacts_menu) {
                    findNavController().navigate(PostListFragmentDirections.navigateToContactsFragment())
                }
                return@setOnMenuItemClickListener true
            }
        }
        refreshData()
    }


    private fun refreshData() {
        viewModel.getAll()
    }

    private fun refreshing(isRefresh: Boolean) {
        binding.refresh.isRefreshing = isRefresh
    }

    override fun onClick(model: Post, position: Int?) {
        findNavController().navigate(PostListFragmentDirections.navigateToComments(model))
    }

}