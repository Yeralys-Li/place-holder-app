package cu.lidev.placeholderapp.presentation.fragment_contacts

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.lidev.placeholderapp.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


sealed class ContactsUiState<out T> {
    object Loading : ContactsUiState<Nothing>()
}

class ContactsViewModel : ViewModel() {

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


    fun deleteContact(contentR: ContentResolver, givenName: String) {
        // First select raw contact id by given name and family name.
        val rawContactId: Long = getRawContactIdByName(contentR, givenName)

        //******************************* delete data table related data ****************************************
        // Data table content process uri.
        val dataContentUri = ContactsContract.Data.CONTENT_URI

        // Create data table where clause.
        val dataWhereClauseBuf = StringBuffer()
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID)
        dataWhereClauseBuf.append(" = ")
        dataWhereClauseBuf.append(rawContactId)

        // Delete all this contact related data in data table.
        contentR.delete(dataContentUri, dataWhereClauseBuf.toString(), null)


        //******************************** delete raw_contacts table related data ***************************************
        // raw_contacts table content process uri.
        val rawContactUri = ContactsContract.RawContacts.CONTENT_URI

        // Create raw_contacts table where clause.
        val rawContactWhereClause = StringBuffer()
        rawContactWhereClause.append(ContactsContract.RawContacts._ID)
        rawContactWhereClause.append(" = ")
        rawContactWhereClause.append(rawContactId)

        // Delete raw_contacts table related data.
        contentR.delete(rawContactUri, rawContactWhereClause.toString(), null)

        //******************************** delete contacts table related data ***************************************
        // contacts table content process uri.
        val contactUri = ContactsContract.Contacts.CONTENT_URI

        // Create contacts table where clause.
        val contactWhereClause = StringBuffer()
        contactWhereClause.append(ContactsContract.Contacts._ID)
        contactWhereClause.append(" = ")
        contactWhereClause.append(rawContactId)

        // Delete raw_contacts table related data.
        contentR.delete(contactUri, contactWhereClause.toString(), null)
    }

    @SuppressLint("Range")
    private fun getRawContactIdByName(contentResolver: ContentResolver, givenName: String): Long {

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        // Create query column array.
        val queryColumnArr = arrayOf(ContactsContract.RawContacts._ID)

        // Create where condition clause.
        val displayName = "$givenName"
        val whereClause =
            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'"

        // Query raw contact id through RawContacts uri.
        val rawContactUri: Uri = ContactsContract.RawContacts.CONTENT_URI

        // Return the query cursor.
        val cursor: Cursor? =
            contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null)
        var rawContactId: Long = -1
        if (cursor != null) {
            // Get contact count that has same display name, generally it should be one.
            val queryResultCount: Int = cursor.getCount()
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if (queryResultCount > 0) {
                // Move to the first row in the result cursor.
                cursor.moveToFirst()
                // Get raw_contact_id.
                rawContactId =
                    cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID))
            }
        }
        return rawContactId
    }


}

