package cu.lidev.core.common.util

import android.view.View

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

/** makes gone a view. */
fun View.gone() {
    visibility = View.GONE
}

