package cu.lidev.core.common.util

interface OnItemClickListener<M> {
    fun onClick(model: M)
    fun onLongClick(model: M) = Unit
    fun onActionClick(model: M) = Unit
}