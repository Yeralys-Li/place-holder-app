package cu.lidev.placeholderapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val idContact: String,
    val name: String,
    val phone: String,
) : Parcelable