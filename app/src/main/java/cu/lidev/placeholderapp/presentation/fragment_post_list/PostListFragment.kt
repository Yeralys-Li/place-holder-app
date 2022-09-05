package cu.lidev.placeholderapp.presentation.fragment_post_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import cu.lidev.core.common.util.RecyclerDecoration
import cu.lidev.core.common.util.progressDialog
import cu.lidev.placeholderapp.databinding.FragmentPostListBinding
import cu.lidev.placeholderapp.presentation.fragment_post_list.components.PostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostListFragment : Fragment() {

    private lateinit var _binding: FragmentPostListBinding
    private val binding get() = _binding
    private val viewModel: PostListViewModel by viewModels()
    private var loadingDialog: AlertDialog? = null

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
            loadingDialog?.dismiss()
            (binding.recycler.adapter as PostAdapter).setItems(it)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    when (it) {
                        is PostListUiState.Loading -> {
                            loadingDialog = progressDialog(requireContext(), isCancelable = false)
                            loadingDialog?.show()
                        }
                        is PostListUiState.Error -> {
                            loadingDialog?.dismiss()
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
            recycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                addItemDecoration(
                    RecyclerDecoration(
                        margin = 16f,
                        columns = 1
                    )
                )
                adapter = PostAdapter()
            }
        }
        viewModel.getAll()
    }

}