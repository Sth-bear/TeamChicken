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

class ContactHeartAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var contactList = mutableListOf<Contact>()
    var viewType:Int = ContactViewModel().getType()

    interface ItemClick {
        fun onClick(view: View, position: Int, contact: Contact)
    }
    var itemClick: ItemClick? = null

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
        return contactList.filter { it.heart }.toMutableList().size
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    fun heartClick(position: Int) {
        contactList.filter { it.heart }[position].heart = !contactList.filter { it.heart }[position].heart
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, itemCount - position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = contactList.filter { it.heart }.toMutableList()[position]

        holder.itemView.setOnClickListener {
            itemClick?.onClick(it,position, contactList.filter { it.heart }.toMutableList()[position])
        }

        when(holder.itemViewType) {
            VIEW_TYPE_LINEAR -> {
                (holder as LinearHolder).bind(currentItem)
                if (contactList.filter { it.heart }[position].uri == null) {
                    holder.image.setImageResource(contactList.filter { it.heart }[position].userImage)
                } else {
                    holder.image.setImageURI(contactList.filter { it.heart }[position].uri)
                }

                holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation)
                holder.heart.setOnClickListener {
                    heartClick(position)
                }
                holder.heart.setImageResource(R.drawable.ic_heart_filled)


            }
            VIEW_TYPE_GRID -> {
                (holder as GridHolder).bind(currentItem)
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
                if (contact.uri == null) {
                    ivItemRvUser.setImageResource(contact.userImage)
                } else {
                    ivItemRvUser.setImageURI(contact.uri)
                }
                tvItemRvName.text = contact.name
            }
        }
    }
//    fun removeItemInHeartFrag(position: Int) {
//        contactList.removeAt(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, itemCount - position)
//
//    }

    fun submitList(items: MutableList<Contact>) {
        items.sortBy { it.name }
        this.contactList = items
        notifyDataSetChanged()
    }


}