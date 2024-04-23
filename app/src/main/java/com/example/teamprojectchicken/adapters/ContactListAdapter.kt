package com.example.teamprojectchicken.adapters

import android.animation.AnimatorInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.graphics.alpha
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.databinding.ItemRvContactList2Binding
import com.example.teamprojectchicken.databinding.ItemRvContactListBinding
import com.example.teamprojectchicken.utils.FormatUtils

class ContactListAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var contactList = mutableListOf<Contact>()
    var viewType:Int = 1
    companion object{
        const val VIEW_TYPE_LINEAR = 1
        const val VIEW_TYPE_GRID = 2
    }

    interface ItemClick {
        fun onClick(view:View, position: Int)
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
        return contactList.size
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.onClick(it,position)
        }
        val currentItem = contactList[position]
        when(holder.itemViewType) {
            VIEW_TYPE_LINEAR -> {
                (holder as LinearHolder).bind(currentItem)
                holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation)
            }
            VIEW_TYPE_GRID -> {
                (holder as GridHolder).bind(currentItem)
            }
        }
    }

    class LinearHolder(private val binding: ItemRvContactListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(contact:Contact) {
            binding.apply {
                ivItemRvUser.setImageResource(contact.userImage)
                tvItemRvName.text = contact.name
                tvItemRvNumber.text = FormatUtils.formatNumber(contact.number)
            }
        }
    }

    class GridHolder(private val binding: ItemRvContactList2Binding): RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.apply {
                ivItemRvUser.setImageResource(contact.userImage)
                tvItemRvName.text = contact.name
            }
        }
    }
}