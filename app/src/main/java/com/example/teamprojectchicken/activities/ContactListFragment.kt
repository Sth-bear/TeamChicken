package com.example.teamprojectchicken.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ContactListAdapter
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
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
    }

    override fun onResume() {
        super.onResume()
        contactListAdapter.notifyItemRangeChanged(0, list.size)
    }
    // plus버튼을 클릭하면 다이얼로그 창이 나타남
    private fun addContact() {
        binding.btnAddPerson.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            val alertDialog: AlertDialog = builder.create()
            val v1 = layoutInflater.inflate(R.layout.addcontact_dialog, null)
            alertDialog.setView(v1)

            val name: EditText? = v1.findViewById(R.id.dl_et_name)
            val number: EditText? = v1.findViewById(R.id.dl_et_number)
            val email: EditText? = v1.findViewById(R.id.dl_et_email)
            val cancel: Button? = v1.findViewById(R.id.dl_btn_cancel)
            val register: Button? = v1.findViewById(R.id.dl_btn_register)
            cancel?.setOnClickListener {
                alertDialog.dismiss()
            }
            register?.setOnClickListener {
                contactListAdapter.contactList.add(Contact(name.toString(), number.toString().toInt(), email.toString(), 0, R.drawable.ic_mine, false))
                contactListAdapter.notifyDataSetChanged()
                alertDialog.dismiss()
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