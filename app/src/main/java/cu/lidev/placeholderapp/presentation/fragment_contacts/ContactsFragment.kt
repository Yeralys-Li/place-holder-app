package cu.lidev.placeholderapp.presentation.fragment_contacts

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import cu.lidev.core.common.util.*
import cu.lidev.placeholderapp.R
import cu.lidev.placeholderapp.databinding.FragmentContactsBinding
import cu.lidev.placeholderapp.domain.model.Contact
import cu.lidev.placeholderapp.presentation.fragment_contacts.components.ContactAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ContactsFragment : Fragment(), OnItemClickListener<Contact> {

    private lateinit var _binding: FragmentContactsBinding
    private val binding get() = _binding
    private val viewModel: ContactsViewModel by viewModels()

    private var loadingDialog: Dialog? = null

    private val requestPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var isAllGranted = false
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                isAllGranted = isGranted
            }
            if (isAllGranted) {
                readContacts()
            } else {
                checkPermission()
            }
        }

    //Setting application
    private val requestSetting =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermission()
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
                adapter = ContactAdapter(this@ContactsFragment)
            }
        }
        checkPermission()
    }


    private fun checkPermissionsIsGranted(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionReadContact
        ) == PackageManager.PERMISSION_GRANTED
        val permission2 = ContextCompat.checkSelfPermission(
            requireContext(),
            permissionWriteContact
        ) == PackageManager.PERMISSION_GRANTED

        return permission1 && permission2
    }

    private fun shouldShowRequestPermissionRationales(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            permissionReadContact
        )
    }

    private fun checkPermission() {
        when {
            checkPermissionsIsGranted() -> {
                readContacts()
            }
            shouldShowRequestPermissionRationales() -> {
                alertDialog(
                    context = requireContext(),
                    title = R.string.permission_required,
                    message = R.string.permission_required_message,
                    isCancelable = false,
                    positiveText = R.string.setting,
                    negativeText = R.string.cancel,
                    negativeListener = { _, _ ->
                        findNavController().popBackStack()
                    },
                    positiveListener = { _, _ ->
                        toAppInformation()
                    }
                ).show()
            }
            else -> {
                requestPermissionsLauncher.launch(
                    arrayOf(permissionReadContact, permissionWriteContact),
                )
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

    override fun onClick(model: Contact, position: Int?) {
        viewModel.deleteContact(requireContext().contentResolver, model.name)
        position?.let { (binding.recycler.adapter as ContactAdapter).deleteItem(it) }
    }

    private fun toAppInformation() {
        requestSetting.launch(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            ).apply {
                data = Uri.fromParts("package", requireActivity().packageName, null)
            }
        )
    }

}