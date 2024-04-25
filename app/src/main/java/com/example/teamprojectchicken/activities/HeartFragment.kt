package com.example.teamprojectchicken.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ContactHeartAdapter
import com.example.teamprojectchicken.adapters.ContactListAdapter.Companion.VIEW_TYPE_LINEAR
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
import com.example.teamprojectchicken.databinding.FragmentHeartBinding
import com.example.teamprojectchicken.utils.FormatUtils.searchFilter
import com.example.teamprojectchicken.viewmodels.ConViewModel
import com.example.teamprojectchicken.viewmodels.ContactViewModel
import com.example.teamprojectchicken.viewmodels.ContactViewModelFactory

class HeartFragment : Fragment() {
    private val binding by lazy { FragmentHeartBinding.inflate(layoutInflater) }
    private val contactHeartAdapter by lazy {
        ContactHeartAdapter()
    }
    private val viewModel = ContactViewModel()
    private val conViewModel by viewModels<ConViewModel> {
        ContactViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        bind()
        itemClick()
        ivOnClick()
        searchContact()
    }

    private fun itemClick() {
        contactHeartAdapter.itemClick = object : ContactHeartAdapter.ItemClick {
            override fun onClick(view: View, position: Int, contact: Contact) {
                val fragment = ContactDetailFragment.newInstance(contact)
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                    .replace(R.id.root2_frag, fragment)
                    .addToBackStack(null)
                    .commit()
            }

        }

    }
    private fun ivOnClick() {
        binding.ivSet.setOnClickListener {
            if (viewModel.getType() == VIEW_TYPE_LINEAR) {
                viewModel.setType()
                contactHeartAdapter.viewType = viewModel.getType()
                gridBind()
            } else {
                viewModel.setType()
                linearBind()
            }
        }
    }

    private fun linearBind() {
        with(binding.rvHeartList) {
            contactHeartAdapter.viewType = viewModel.getType()
            adapter = contactHeartAdapter
            layoutManager = LinearLayoutManager(requireContext())
            binding.ivSet.setImageResource(R.drawable.ic_list)
            itemTouch.attachToRecyclerView(this)
        }
    }

    private fun gridBind() {
        with(binding.rvHeartList) {
            adapter = contactHeartAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
            binding.ivSet.setImageResource(R.drawable.ic_grid)
        }
    }

    private fun bind() {
        with(binding.rvHeartList) {
            adapter = contactHeartAdapter
            layoutManager = if (viewModel.getType() == VIEW_TYPE_LINEAR) {
                LinearLayoutManager(requireContext())
            } else {
                GridLayoutManager(requireContext(), 4)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        contactHeartAdapter.notifyDataSetChanged()
    }

    private fun observeLiveData(){
        conViewModel.contactLiveData.observe(viewLifecycleOwner, Observer { contactList ->
            contactHeartAdapter.submitList(contactList)
        })
    }

    private fun searchContact(){
        binding.svHeartSearch.isSubmitButtonEnabled = true
        binding.svHeartSearch.setOnQueryTextListener(object :SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactHeartAdapter.submitList(searchFilter(newText))
                return true
            }
        })
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
            contactHeartAdapter.notifyDataSetChanged()
        }
    })
}