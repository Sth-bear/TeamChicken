package com.example.teamprojectchicken.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.databinding.ItemRvContactList2Binding
import com.example.teamprojectchicken.databinding.ItemRvContactListBinding
import com.example.teamprojectchicken.utils.FormatUtils
import com.example.teamprojectchicken.utils.FormatUtils.VIEW_TYPE_GRID
import com.example.teamprojectchicken.utils.FormatUtils.VIEW_TYPE_LINEAR
import com.example.teamprojectchicken.viewmodels.ContactViewModel

class ContactListAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var contactList = mutableListOf<Contact>()
    var viewType:Int = ContactViewModel().getType()

    interface ItemClick {
        fun onClick(view:View, position: Int, contact: Contact)
        fun longClick(position: Int)
    }
    var itemClick: ItemClick? = null

    fun removeItem(position: Int) {
        contactList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, itemCount - position)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_LINEAR -> {
                val binding = ItemRvContactListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LinearHolder(binding)
            }
            VIEW_TYPE_GRID -> {
                val binding = ItemRvContactList2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                GridHolder(binding)
            }
            else -> throw IllegalArgumentException("알 수 없는 뷰 타입")
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    fun heartClick(position: Int) {
        contactList[position].heart = !contactList[position].heart
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it,position, contactList[position])
        }

        holder.itemView.setOnLongClickListener {
            itemClick?.longClick(position)
            false
        }

        val currentItem = contactList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_LINEAR -> {
                (holder as LinearHolder).bind(currentItem)
                if (contactList[position].uri == null) {
                    holder.image.setImageResource(contactList[position].userImage)
                } else {
                    holder.image.setImageURI(contactList[position].uri)
                }

                holder.itemView.animation =
                    AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation)
                holder.heart.setOnClickListener {
                    heartClick(position)
                }

                if (contactList[position].heart) {
                    holder.heart.setImageResource(R.drawable.ic_heart_filled)
                } else {
                    holder.heart.setImageResource(R.drawable.ic_heart)
                }

            }

            VIEW_TYPE_GRID -> {
                (holder as GridHolder).bind(currentItem)
                if (contactList[position].uri == null) {
                    holder.image.setImageResource(contactList[position].userImage)
                } else {
                    holder.image.setImageURI(contactList[position].uri)
                }
            }

        }
    }

    class LinearHolder(private val binding: ItemRvContactListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(contact:Contact) {
            binding.apply {
                tvItemRvName.text = contact.name
                tvItemRvNumber.text = FormatUtils.formatNumber(contact.number)
            }
        }
        var heart = binding.btnRvHeart
        var image = binding.ivItemRvUser
    }

    class GridHolder(private val binding: ItemRvContactList2Binding): RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.apply {
                tvItemRvName.text = contact.name
            }
        }
        var image = binding.ivItemRvUser
    }

    fun submitList(items: MutableList<Contact>) {
        this.contactList = items.sortedBy { it.name }.toMutableList()
        notifyDataSetChanged()
    }
}