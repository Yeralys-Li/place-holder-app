package cu.lidev.placeholderapp.presentation.fragment_contacts

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cu.lidev.core.common.util.RecyclerDecoration
import cu.lidev.core.common.util.permissionReadContact
import cu.lidev.core.common.util.progressDialog
import cu.lidev.placeholderapp.databinding.FragmentContactsBinding
import cu.lidev.placeholderapp.domain.model.Contact
import cu.lidev.placeholderapp.presentation.fragment_contacts.components.ContactAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val FROM_COLUMNS: Array<String> = arrayOf(
    ContactsContract.Contacts.DISPLAY_NAME
)

class ContactsFragment : Fragment() {

    private lateinit var _binding: FragmentContactsBinding
    private val binding get() = _binding
    private val viewModel: ContactsViewModel by viewModels()

    private val contacts = arrayListOf<Contact>()
    private var loadingDialog: Dialog? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                readContacts()
            } else {
                Log.i("Permission: ", "Denied")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        initUI()
        subscribeUi()
        return binding.root
    }


    private fun initUI() {
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
                adapter = ContactAdapter()
            }
        }
        checkPermission()
    }


    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permissionReadContact
            ) == PackageManager.PERMISSION_GRANTED -> {
                readContacts()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                permissionReadContact
            ) -> {
                //TODO
            }
            else -> {
                requestPermissionLauncher.launch(permissionReadContact)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeUi() {
        viewModel.data.observe(viewLifecycleOwner) { it ->
            loadingDialog?.dismiss()
            binding.apply {
                total.text = "Total " + it.size.toString()
                (recycler.adapter as ContactAdapter).setItems(it)
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    when (it) {
                        is ContactsUiState.Loading -> {
                            loadingDialog =
                                progressDialog(context = requireContext(), isCancelable = false)
                            loadingDialog?.show()
                        }
                    }
                }
        }
    }

    @SuppressLint("Range")
    private fun readContacts() {
        viewModel.getAll(requireContext().contentResolver)
    }

}