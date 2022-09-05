package cu.lidev.core.common.util

import android.graphics.Rect
import android.view.View
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

open class RecyclerDecoration(
    @FloatRange(from = 0.0) private val margin: Float,
    @IntRange(from = 1) private val columns: Int = 1,
) : ItemDecoration() {

    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildLayoutPosition(view)
        outRect.right = margin.toInt() //set right margin to all
        outRect.bottom = (margin / 1.5).toInt() //set bottom margin to all
        if (pos < columns) outRect.top =
            (margin / 1.5).toInt() //we only add top margin to the first row
        if (pos % columns == 0) outRect.left =
            margin.toInt() //add left margin only to the first column
    }

}

class RecyclerNoDecoration(
    @FloatRange(from = 0.0) private val margin: Float,
    @IntRange(from = 0) private val columns: Int = 1,
) : RecyclerDecoration(margin, columns) {

    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildLayoutPosition(view)
        if (pos < columns) outRect.top = margin.toInt() //we only add top margin to the first row
    }

}