package com.example.teamprojectchicken.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teamprojectchicken.activities.ContactListFragment.Companion.list
import com.example.teamprojectchicken.adapters.ContactHeartAdapter
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.databinding.FragmentHeartBinding
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
                    }
                } else {
                    viewModel.setType()
                    with(binding.rvHeartList) {
                        contactHeartAdapter.viewType = viewModel.getType()
                        adapter = contactHeartAdapter
                        layoutManager = LinearLayoutManager(requireContext())
                    }
                }
            }
        }
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
        contactHeartAdapter.notifyDataSetChanged()
    }

    private fun filter(serchText:String?) {
        val filteredList = ArrayList<Contact>()
        for(item in list) {
            if (item.heart == true && item.name.contains(serchText?: "")) {
                filteredList.add(item)
            }
        }
        Log.d("test2", "filter: ${filteredList}")
        contactHeartAdapter.contactList = filteredList
        contactHeartAdapter.notifyDataSetChanged()
    }
}