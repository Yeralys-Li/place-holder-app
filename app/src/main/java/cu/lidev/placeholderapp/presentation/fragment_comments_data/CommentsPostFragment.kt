package cu.lidev.placeholderapp.presentation.fragment_comments_data

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import cu.lidev.core.common.util.RecyclerDecoration
import cu.lidev.core.common.util.progressDialog
import cu.lidev.placeholderapp.databinding.FragmentCommentsPostBinding
import cu.lidev.placeholderapp.presentation.fragment_comments_data.components.CommentsAdapter
import cu.lidev.placeholderapp.presentation.fragment_post_list.PostListUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CommentsPostFragment : Fragment() {

    private var postId: Int = -1
    private lateinit var _binding: FragmentCommentsPostBinding
    private val binding get() = _binding
    private val viewModel: CommentsPostViewModel by viewModels()
    private var loadingDialog: Dialog? = null
    private val args by navArgs<CommentsPostFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsPostBinding.inflate(inflater, container, false)
        initUI()
        subscribeUi()
        return binding.root
    }

    private fun subscribeUi() {
        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }
        viewModel.data.observe(viewLifecycleOwner) {
            loadingDialog?.dismiss()
            (binding.recycler.adapter as CommentsAdapter).setItems(it)
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
        postId = args.post.id
        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            recycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                addItemDecoration(
                    RecyclerDecoration(
                        margin = 0.9f,
                        columns = 1
                    )
                )
                adapter = CommentsAdapter()
            }
        }
        refreshData()
    }

    private fun refreshData() {
        viewModel.getComments(postId = postId)
    }


}