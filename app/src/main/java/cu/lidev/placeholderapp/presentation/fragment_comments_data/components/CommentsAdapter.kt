package cu.lidev.placeholderapp.presentation.fragment_comments_data.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cu.lidev.placeholderapp.databinding.ItemCommentBinding
import cu.lidev.placeholderapp.domain.model.Comment


class CommentsAdapter :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var items = listOf<Comment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun getItem(position: Int) = items[position]

    fun setItems(items: List<Comment>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(comment: Comment) = binding.run {
            name.text = comment.name
            email.text = comment.email
            body.text = comment.body
        }
    }


}