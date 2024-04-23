package com.example.teamprojectchicken.activities

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.databinding.FragmentContactDetailBinding
import com.example.teamprojectchicken.databinding.FragmentMyPageBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_CONTACT = "contact"


/**
 * A simple [Fragment] subclass.
 * Use the [ContactDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
                etDetailPhoneNumber.setText(contact.number.toString())
                etDetailBirth.setText(contact.date.toString())
                etDetailEmail.setText(contact.email)
                ivDetailProfile.setImageResource(contact.userImage)
            }
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param contact Parameter 1. parcelable data
         * @return A new instance of fragment ContactDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(contact: Contact) =
            ContactDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CONTACT, contact)
                }
            }
    }
}
