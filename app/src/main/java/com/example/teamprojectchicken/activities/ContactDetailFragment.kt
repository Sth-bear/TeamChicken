package com.example.teamprojectchicken.activities

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.databinding.FragmentContactDetailBinding
import com.example.teamprojectchicken.utils.FormatUtils
import com.example.teamprojectchicken.viewmodels.ContactViewModel

private const val ARG_CONTACT = "contact"


class ContactDetailFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback
    private var viewModel = ContactViewModel()
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
        var heart = contact?.heart

        // 라이브데이터에 contact.heart 저장
        if (heart != null) {
            viewModel.getData(heart)
        }

        contact?.let { contact ->
            binding.apply {
                etDetailName.setText(contact.name)
                etDetailPhoneNumber.setText(FormatUtils.formatNumber(contact.number))
                etDetailBirth.setText(contact.date.toString())
                etDetailEmail.setText(contact.email)
                ivDetailProfile.setImageResource(contact.userImage)

                // 클릭시 뷰모델의 라이브데이터에 Not을 입력
                btnDetailHeart.setOnClickListener {
                    heart = heart?.not()
                    heart?.let { it1 -> viewModel.setData(it1) }
                }

                // 라이브 데이터를 가져와서 이미지 세팅
                val observer = Observer<Boolean> {
                    if (it) {
                        btnDetailHeart.setImageResource(R.drawable.ic_heart_filled)
                    } else {
                        btnDetailHeart.setImageResource(R.drawable.ic_heart)
                    }
                }
                viewModel.liveData.observe(viewLifecycleOwner, observer)
            }

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 수정된 아이콘 설정을 다른 프래그먼트로 보냄
                contact?.heart = viewModel.liveData.value == true
                setFragmentResult(ContactListFragment.REQUEST_KEY, bundleOf(ContactListFragment.BUNDLE_KEY to contact))
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
