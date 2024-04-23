package com.example.teamprojectchicken.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.databinding.ItemRvContactListBinding
import com.example.teamprojectchicken.utils.FormatUtils

class ContactListAdapter(private val onClick: (Contact) -> Unit) : RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>() {
    var contactList = mutableListOf<Contact>()
    class ContactViewHolder(private var binding : ItemRvContactListBinding, val onClick: (Contact) -> Unit, val layout: View) : RecyclerView.ViewHolder(binding.root) {
        private var currentContact: Contact ?= null
        init {
            itemView.setOnClickListener {
                currentContact?.let(onClick)

            }
        }
        fun bind(contact: Contact) {
            currentContact = contact
            binding.ivItemRvUser.setImageResource(contact.userImage)
            binding.tvItemRvName.text = contact.name
            binding.tvItemRvNumber.text = FormatUtils.formatNumber(contact.number)
        }

        var heart = binding.btnRvHeart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_contact_list,parent, false)
        return ContactViewHolder(ItemRvContactListBinding.bind(view), onClick, view)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onClick(contactList[position])
        }

        holder.layout.animation = AnimationUtils.loadAnimation(holder.layout.context, R.anim.item_animation)
        holder.bind(contactList[position])

        holder.heart.setOnClickListener {
            heartClick(position)
        }

        if (contactList[position].heart) {
            holder.heart.setImageResource(R.drawable.ic_heart_filled)
        } else {
            holder.heart.setImageResource(R.drawable.ic_heart)
        }
    }

    fun heartClick(position: Int) {
        contactList[position].heart = !contactList[position].heart
        notifyItemChanged(position)
    }
}