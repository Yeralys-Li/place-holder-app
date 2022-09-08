package cu.lidev.placeholderapp.presentation.fragment_contacts.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cu.lidev.core.common.util.OnItemClickListener
import cu.lidev.placeholderapp.databinding.ItemContactBinding
import cu.lidev.placeholderapp.domain.model.Contact


class ContactAdapter(val itemClickListener: OnItemClickListener<Contact>? = null) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private var items = arrayListOf<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun getItem(position: Int) = items[position]

    fun setItems(items: List<Contact>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) = binding.run {
            name.text = contact.name
            phone.text = contact.phone
            delete.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    itemClickListener?.onClick(
                        contact,
                        position = bindingAdapterPosition
                    )
                }
            }
        }
    }
}

