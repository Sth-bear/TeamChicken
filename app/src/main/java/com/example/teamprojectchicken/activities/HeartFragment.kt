package com.example.teamprojectchicken.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.example.teamprojectchicken.utils.FormatUtils.searchFilter
import com.example.teamprojectchicken.viewmodels.ContactViewModel

class HeartFragment : Fragment() {
    private val binding by lazy { FragmentHeartBinding.inflate(layoutInflater) }
    private val contactHeartAdapter by lazy {
        ContactHeartAdapter()
    }
    private val viewModel = ContactViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 초기 어댑터 설정과 데이터 설정
        contactHeartAdapter.contactList = list
        with(binding.rvHeartList) {
            adapter = contactHeartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.rvHeartList) {
            adapter = contactHeartAdapter
            layoutManager = if (viewModel.getType() == 1) {
                LinearLayoutManager(requireContext())
            } else {
                GridLayoutManager(requireContext(),4)
            }
            binding.ivSet.setOnClickListener {
                if (viewModel.getType() == 1) {
                    viewModel.setType()
                    contactHeartAdapter.viewType = viewModel.getType()
                    with(binding.rvHeartList) {
                        adapter = contactHeartAdapter
                        layoutManager = GridLayoutManager(requireContext(), 4)
                        binding.ivSet.setImageResource(com.example.teamprojectchicken.R.drawable.ic_grid)
                    }
                } else {
                    viewModel.setType()
                    with(binding.rvHeartList) {
                        contactHeartAdapter.viewType = viewModel.getType()
                        adapter = contactHeartAdapter
                        layoutManager = LinearLayoutManager(requireContext())
                        binding.ivSet.setImageResource(com.example.teamprojectchicken.R.drawable.ic_list)
                        itemTouch.attachToRecyclerView(this)
                    }
                }
            }
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
        binding.svHeartSearch.isSubmitButtonEnabled = true
        binding.svHeartSearch.setOnQueryTextListener(object :SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                contactHeartAdapter.contactList = searchFilter(newText)
                contactHeartAdapter.notifyDataSetChanged()
                return true
            }
        })
    }

    override fun onPause() {
        super.onPause()
        contactHeartAdapter.notifyDataSetChanged()
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