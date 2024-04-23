package com.example.teamprojectchicken.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.databinding.FragmentContactDetailBinding
import com.example.teamprojectchicken.utils.FormatUtils


private const val ARG_CONTACT = "contact"


class ContactDetailFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback
    private var contact: Contact? = null
    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contact = it.getParcelable(ARG_CONTACT, Contact::class.java)
        }
    }

    // 뷰 바인딩
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    //ContactListFragment에서 받아온 값 출력
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contact?.let { contact ->
            binding.apply {
                etDetailName.setText(contact.name)
                etDetailPhoneNumber.setText(FormatUtils.formatNumber(contact.number))
                etDetailBirth.setText(contact.date.toString())
                etDetailEmail.setText(contact.email)
                ivDetailProfile.setImageResource(contact.userImage)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }


    companion object {
        @JvmStatic
        fun newInstance(contact: Contact) =
            ContactDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CONTACT, contact)
                }
            }
    }
}
