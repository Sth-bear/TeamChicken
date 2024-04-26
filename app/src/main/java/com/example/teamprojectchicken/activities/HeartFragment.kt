package com.example.teamprojectchicken.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.activities.ContactListFragment.Companion.list
import com.example.teamprojectchicken.adapters.ContactHeartAdapter
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
import com.example.teamprojectchicken.databinding.FragmentHeartBinding
import com.example.teamprojectchicken.utils.FormatUtils.VIEW_TYPE_LINEAR
import com.example.teamprojectchicken.utils.isvisible
import com.example.teamprojectchicken.viewmodels.ContactViewModel

class HeartFragment : Fragment() {
    private val binding by lazy { FragmentHeartBinding.inflate(layoutInflater) }
    private val contactHeartAdapter by lazy {
        ContactHeartAdapter()
    }
    private val viewModel = ContactViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindDefault()
        ivSetOnClick()
        goToHeartDetail()
        goToCall()
    }
    private fun bindDefault() {
        contactHeartAdapter.submitList(list)
        with(binding.rvHeartList) {
            adapter = contactHeartAdapter
            layoutManager = if (viewModel.getType() == VIEW_TYPE_LINEAR) {
                LinearLayoutManager(requireContext())
            } else {
                GridLayoutManager(requireContext(),4)
            }
        }
    }
    private fun ivSetOnClick() {
        binding.ivSet.setOnClickListener {
            if (viewModel.getType() == VIEW_TYPE_LINEAR) {
                gridBind()
            } else {
                viewModel.setType()
                linearBind()
            }
        }
    }

    private fun gridBind() {
        viewModel.setType()
        contactHeartAdapter.viewType = viewModel.getType()
        with(binding.rvHeartList) {
            adapter = contactHeartAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
            binding.ivSet.setImageResource(R.drawable.ic_grid)
        }
    }
    private fun linearBind() {
        viewModel.setType()
        with(binding.rvHeartList) {
            contactHeartAdapter.viewType = viewModel.getType()
            adapter = contactHeartAdapter
            layoutManager = LinearLayoutManager(requireContext())
            binding.ivSet.setImageResource(R.drawable.ic_list)
            itemTouch.attachToRecyclerView(this)
        }
    }
    private fun goToHeartDetail() {
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

    private fun goToCall() {
        binding.svHeartSearch.isSubmitButtonEnabled = true
        binding.svHeartSearch.setOnQueryTextListener(object :SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as? FragmentActivity)?.isvisible()
    }

    private fun filter(text:String?) {
        val searchText = text?.replace("-","")
        val filteredList = ArrayList<Contact>()
        for(item in list) {
            if(item.name.contains(searchText?:"") || "0${item.number}".contains(searchText?:"")) {
                filteredList.add(item)
            }
        }
        contactHeartAdapter.submitList(filteredList)
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