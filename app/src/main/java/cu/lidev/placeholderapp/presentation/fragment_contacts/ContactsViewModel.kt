package cu.lidev.placeholderapp.presentation.fragment_contacts

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lidev.placeholderapp.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class ContactsUiState<out T> {
    object Loading : ContactsUiState<Nothing>()
}

class ContactsViewModel  : ViewModel() {

    private val _uiState = Channel<ContactsUiState<Nothing>>()
    val uiState: Flow<ContactsUiState<Nothing>> = _uiState.receiveAsFlow()

    private val _data: MutableLiveData<List<Contact>> by lazy { MutableLiveData() }
    val data: LiveData<List<Contact>> = _data

    @SuppressLint("Range")
    fun getAll(contentR: ContentResolver) = viewModelScope.launch {
        _uiState.send(ContactsUiState.Loading)

        var name = ""
        var phone = ""
        val contacts = arrayListOf<Contact>()
        withContext(Dispatchers.IO) {
            val cursor = contentR.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                "display_name ASC"
            )
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        val cursorInfo = contentR.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        val person =
                            ContentUris.withAppendedId(
                                ContactsContract.Contacts.CONTENT_URI,
                                id.toLong()
                            )
                        while (cursorInfo != null && cursorInfo.moveToNext()) {
                            name =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                            phone =
                                cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        }
                        cursorInfo?.close()

                        contacts.add(
                            Contact(
                                idContact = id,
                                name = name,
                                phone = phone
                            )
                        )
                    }
                }
                cursor.close()
            }
        }

        _data.value = contacts
    }
}