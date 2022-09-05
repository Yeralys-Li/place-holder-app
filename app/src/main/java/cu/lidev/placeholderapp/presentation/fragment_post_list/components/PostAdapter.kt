package cu.lidev.placeholderapp.presentation.fragment_post_list.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cu.lidev.placeholderapp.databinding.ItemPostBinding
import cu.lidev.placeholderapp.domain.model.Post

class PostAdapter() :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var items = listOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun getItem(position: Int) = items[position]

    fun setItems(items: List<Post>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) = binding.run {
            title.text = post.title
            body.text = post.body
        }
    }


}