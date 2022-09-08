package cu.lidev.core.common.util

interface OnItemClickListener<M> {
    fun onClick(model: M,position:Int?=null)
    fun onLongClick(model: M) = Unit
    fun onActionClick(model: M) = Unit
}