package com.example.teamprojectchicken.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ContactListAdapter
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
import com.example.teamprojectchicken.databinding.FragmentContactListBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ContactListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentContactListBinding.inflate(layoutInflater) }
    private val contactListAdapter by lazy {
        ContactListAdapter()
    }
    private var layoutStyle:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        contactListAdapter.contactList = DataSource.getDataSource().getContactList()
        contactListAdapter.viewType = 1
        with(binding.rvListList) {
            adapter = contactListAdapter
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
        binding.ivSet.setOnClickListener {
            if(layoutStyle == 1) {
                layoutStyle = 2
                contactListAdapter.viewType = layoutStyle
                with(binding.rvListList) {
                    adapter = contactListAdapter
                    layoutManager = GridLayoutManager(requireContext(), 4)
                }
            } else {
                layoutStyle = 1
                with(binding.rvListList) {
                    contactListAdapter.viewType = layoutStyle
                    adapter = contactListAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
        }
        contactListAdapter.itemClick = object : ContactListAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val fragment = ContactDetailFragment.newInstance(DataSource.getDataSource().getContactList()[position])
                parentFragmentManager.beginTransaction()
                    .replace(R.id.root_frag, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}