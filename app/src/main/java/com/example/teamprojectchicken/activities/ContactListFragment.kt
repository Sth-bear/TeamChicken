package com.example.teamprojectchicken.activities

import android.app.ActivityOptions
import android.os.Bundle
import android.util.Log
import android.util.Pair
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ContactListAdapter
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
import com.example.teamprojectchicken.databinding.FragmentContactListBinding
import com.google.android.material.snackbar.Snackbar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ContactListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentContactListBinding.inflate(layoutInflater) }
    private val contactListAdapter by lazy { ContactListAdapter{
        adapterOnClick(it)
    } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        contactListAdapter.contactList = DataSource.getDataSource().getContactList()
        with(binding.rvListList) {
            adapter = contactListAdapter
            layoutManager = LinearLayoutManager(requireContext())
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

    // ContactDetailFragment 데이터 전달
    fun adapterOnClick(contact: Contact) {
        val detailFragment = ContactDetailFragment.newInstance(contact)
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            hide(this@ContactListFragment)
            add(R.id.main, detailFragment)
            addToBackStack(null)
            commit()
        }
    }


}