package com.example.teamprojectchicken.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
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
import com.example.teamprojectchicken.utils.FormatUtils.VIEW_TYPE_LINEAR
import com.example.teamprojectchicken.utils.isvisible
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactListAdapter.submitList(list)
        bind()
        addContact()
        ivSetOnClick()
        itemOnClick()
        searchContact()
    }

    override fun onResume() {
        super.onResume()
        (activity as? FragmentActivity)?.isvisible()
    }

    private fun bind() {
        with(binding.rvListList) {
            adapter = contactListAdapter
            if (viewModel.getType() == VIEW_TYPE_LINEAR) {
                binding.ivSet.setImageResource(R.drawable.ic_list)
                layoutManager = LinearLayoutManager(requireContext())
                itemTouch.attachToRecyclerView(this)
            } else {
                layoutManager = GridLayoutManager(requireContext(),4)
                binding.ivSet.setImageResource(R.drawable.ic_grid)
            }
        }
    }

    private fun ivSetOnClick() {
        binding.ivSet.setOnClickListener {
            if (viewModel.getType() == VIEW_TYPE_LINEAR) {
                viewModel.setType()
                contactListAdapter.viewType = viewModel.getType()
                gridLayout()
            } else {
                viewModel.setType()
                linearLayout()
            }
        }
    }
    private fun linearLayout() {
        with(binding.rvListList) {
            contactListAdapter.viewType = viewModel.getType()
            adapter = contactListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            binding.ivSet.setImageResource(R.drawable.ic_list)
            itemTouch.attachToRecyclerView(this)
        }
    }
    private fun gridLayout() {
        with(binding.rvListList) {
            adapter = contactListAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
            binding.ivSet.setImageResource(R.drawable.ic_grid)
        }
    }
    private fun itemOnClick() {
        contactListAdapter.itemClick = object : ContactListAdapter.ItemClick {
            override fun onClick(view: View, position: Int, contact: Contact) {
                goToDetailContact(contact)

            }
            override fun longClick(position: Int) {
                delContact(position)
            }
        }
    }
    private fun goToDetailContact(contact: Contact){
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
    private fun delContact(position:Int) {
        var builder = AlertDialog.Builder(requireContext())
        builder.setTitle("연락처 삭제")
        builder.setMessage("해당 연락처를 삭제합니다.")

        val listener = DialogInterface.OnClickListener { p0, p1 ->
            // 다이얼로그 인터페이스 생성, 버튼 클릭시 처리 이벤트
            if (p1 == DialogInterface.BUTTON_POSITIVE ) {
                contactListAdapter.removeItem(position)
                Toast.makeText(requireContext(), "연락처 삭제 완료", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", null)
        builder.show()
    }
    private fun searchContact() {
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
    private fun filter(text : String?) {
        val searchText = text?.replace("-","")
        val filteredList = ArrayList<Contact>()
        for(item in list) {
            if(item.name.contains(searchText?:"") || "0${item.number}".contains(searchText?:"")) {
                filteredList.add(item)
            }
        }
        contactListAdapter.submitList(filteredList)
    }
    override fun onPause() {
        super.onPause()
        contactListAdapter.notifyItemRangeChanged(0, list.size)
    }
    // 연락처 추가 다이얼로그
    private fun addContact() {
        binding.btnAddPerson.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            val alertDialog: AlertDialog = builder.create()
            val binding: AddcontactDialogBinding = AddcontactDialogBinding.inflate(layoutInflater)
            alertDialog.setView(binding.root)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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