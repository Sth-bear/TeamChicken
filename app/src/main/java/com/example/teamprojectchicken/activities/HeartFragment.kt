package com.example.teamprojectchicken.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ContactListAdapter
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
import com.example.teamprojectchicken.databinding.FragmentHeartBinding
class HeartFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentHeartBinding.inflate(layoutInflater) }
    private val contactListAdapter by lazy {
        ContactListAdapter()
    }
    private var layoutStyle: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 초기 어댑터 설정과 데이터 설정
        contactListAdapter.contactList = DataSource.getDataSource().getContactList()
        contactListAdapter.viewType = 1
        with(binding.rvHeartList) {
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
        with(binding.rvHeartList) {
            adapter = contactListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            binding.ivSet.setOnClickListener {
                if (layoutStyle == 1) {
                    layoutStyle = 2
                    contactListAdapter.viewType = layoutStyle
                    with(binding.rvHeartList) {
                        adapter = contactListAdapter
                        layoutManager = GridLayoutManager(requireContext(), 4)
                    }
                } else {
                    layoutStyle = 1
                    with(binding.rvHeartList) {
                        contactListAdapter.viewType = layoutStyle
                        adapter = contactListAdapter
                        layoutManager = LinearLayoutManager(requireContext())
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
}