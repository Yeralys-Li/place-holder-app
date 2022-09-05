package cu.lidev.core.common.util

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cu.lidev.core.R


fun dialogBuilder(
    context: Context,
    @StringRes title: Int? = null, @StringRes message: Int? = null,
    @StringRes positiveText: Int? = null, @StringRes negativeText: Int? = null,
    @DrawableRes icon: Int? = null, isCancelable: Boolean? = true,
    positiveListener: DialogInterface.OnClickListener? = null,
    negativeListener: DialogInterface.OnClickListener? = null
): MaterialAlertDialogBuilder {
    val builder = MaterialAlertDialogBuilder(context)
    isCancelable?.let { builder.setCancelable(it) }
    message?.let { builder.setMessage(it) }
    icon?.let { builder.setIcon(it) }
    title?.let { builder.setTitle(it) }
    positiveText?.let { builder.setPositiveButton(it, positiveListener) }
    negativeText?.let { builder.setNegativeButton(it, negativeListener) }
    return builder
}

fun progressDialog(
    context: Context, @StringRes title: Int? = null, @StringRes message: Int? = null,
    @DrawableRes icon: Int? = null, isCancelable: Boolean? = true
) = dialogBuilder(
    context, title, message, null, null, icon, isCancelable,
    null, null
).setView(R.layout.loading_layout).create()