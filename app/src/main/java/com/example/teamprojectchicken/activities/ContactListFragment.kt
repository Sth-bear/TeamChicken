package com.example.teamprojectchicken.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
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
        ContactListAdapter {
            adapterOnClick(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // 초기 어댑터 설정과 데이터 설정
        contactListAdapter.contactList = DataSource.getDataSource().getContactList()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        contactListAdapter.contactList = DataSource.getDataSource().getContactList()
        with(binding.rvListList) {
            adapter = contactListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // 바뀐 데이터 받아오기
        setFragmentResultListener(REQUEST_KEY) { requestKey, bundle ->
            val contact = bundle.getParcelable<Contact>(BUNDLE_KEY)
        }



    }

    companion object {
        const val REQUEST_KEY = "requestKey"
        const val BUNDLE_KEY = "bundleKey"
    }

    // ContactDetailFragment 데이터 전달
    fun adapterOnClick(contact: Contact) {
        val fragment = ContactDetailFragment.newInstance(contact)
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in,R.anim.fade_out)
            .replace(R.id.root_frag, fragment)
            .addToBackStack(null)
            .commit()
    }
}