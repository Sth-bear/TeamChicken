package com.example.teamprojectchicken.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ContactListAdapter
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
import com.example.teamprojectchicken.databinding.AddcontactDialogBinding
import com.example.teamprojectchicken.databinding.FragmentContactListBinding
import com.example.teamprojectchicken.viewmodels.ContactViewModel
class ContactListFragment : Fragment() {
    private val binding by lazy { FragmentContactListBinding.inflate(layoutInflater) }
    private val viewModel = ContactViewModel()
    val contactListAdapter by lazy {
        ContactListAdapter()
    }
    companion object {
        var list: MutableList<Contact> = DataSource.getDataSource().getContactList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactListAdapter.contactList = list
        with(binding.rvListList) {
            adapter = contactListAdapter
            if (viewModel.getType() == 1) {
                binding.ivSet.setImageResource(R.drawable.ic_list)
                layoutManager = LinearLayoutManager(requireContext())
                itemTouch.attachToRecyclerView(this)
            } else {
                layoutManager = GridLayoutManager(requireContext(),4)
                binding.ivSet.setImageResource(R.drawable.ic_grid)
            }
        }

        addContact()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.rvListList) {
            adapter = contactListAdapter
            layoutManager = if (viewModel.getType() == 1) {
                LinearLayoutManager(requireContext())
            } else {
                GridLayoutManager(requireContext(),4)
            }
            binding.ivSet.setOnClickListener {
                if (viewModel.getType() == 1) {
                    viewModel.setType()
                    contactListAdapter.viewType = viewModel.getType()
                    with(binding.rvListList) {
                        adapter = contactListAdapter
                        layoutManager = GridLayoutManager(requireContext(), 4)
                        binding.ivSet.setImageResource(R.drawable.ic_grid)
                    }
                } else {
                    viewModel.setType()
                    with(binding.rvListList) {
                        contactListAdapter.viewType = viewModel.getType()
                        adapter = contactListAdapter
                        layoutManager = LinearLayoutManager(requireContext())
                        binding.ivSet.setImageResource(R.drawable.ic_list)
                        itemTouch.attachToRecyclerView(this)
                    }
                }
            }
            contactListAdapter.itemClick = object : ContactListAdapter.ItemClick {
                override fun onClick(view: View, position: Int, contact: Contact) {
                    val fragment = ContactDetailFragment.newInstance(contact)
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                        )
                        .replace(R.id.root_frag, fragment)
                        .addToBackStack(null)
                        .commit()
                }

            }
        }
        binding.svListSearch.isSubmitButtonEnabled = true
        binding.svListSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    private fun filter(searchText : String?) {
        val filteredList = ArrayList<Contact>()
        for(item in list) {
            if(item.name.contains(searchText?:"")) {
                filteredList.add(item)
            }
        }
        Log.d("test", "filter: ${filteredList}")
        contactListAdapter.contactList = filteredList.toMutableList()
        contactListAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        contactListAdapter.notifyItemRangeChanged(0, list.size)
    }
    // 연락처 추가 다이얼로그
    private fun addContact() {
        binding.btnAddPerson.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            val alertDialog: AlertDialog = builder.create()
            val binding: AddcontactDialogBinding = AddcontactDialogBinding.inflate(layoutInflater)
            alertDialog.setView(binding.root)

            binding.dlBtnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            binding.dlBtnRegister.setOnClickListener {
                val name = binding.dlEtName.text.toString()
                val number = binding.dlEtNumber.text.toString()
                val email = binding.dlEtEmail.text.toString()
                if (name.isNotEmpty() && number.isNotEmpty() && email.isNotEmpty()) {
                    try {
                        if (number.toInt() is Int) {
                            contactListAdapter.contactList.add(
                                Contact(
                                    name = name,
                                    number = number.toInt(),
                                    email = email,
                                    date = 20000000,
                                    userImage = R.drawable.ic_mine,
                                    heart = false,
                                    uri = null
                                )
                            )
                            contactListAdapter.notifyDataSetChanged()
                            Toast.makeText(requireContext(), "연락처를 추가했습니다.", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        }
                    } catch (e:java.lang.NumberFormatException) {
                        Toast.makeText(requireContext(), "11자리 이하의 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } else if (name.isEmpty() || number.isEmpty() || email.isEmpty()) {
                    Toast.makeText(requireContext(), "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            alertDialog.show()
        }
    }


    val itemTouch = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val phoneNumber = DataSource.getDataSource().getContactList()[position].number
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:0$phoneNumber")
            }
            viewHolder.itemView.context.startActivity(intent)

            viewHolder.itemView.translationX = 0f // 아이템이 사라지는 마술
            contactListAdapter.notifyDataSetChanged()
        }
    })

}