package com.example.teamprojectchicken.activities

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.viewpager2.widget.ViewPager2
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.databinding.FragmentContactListBinding
import com.example.teamprojectchicken.databinding.FragmentMyPageBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyPageFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentMyPageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
        editInfo()
    }

    //    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                val viewPager = requireActivity().findViewById<ViewPager2>(R.id.vp_main)
//                viewPager.currentItem = 0
//            }
//        }
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        callback.remove()
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    //정보 수정하는 부분
    fun editInfo() {
        var isEditable = false
        binding.btnMySave.setOnClickListener {
            if (!isEditable) {
                isEditable = true
                binding.btnMySave.text = "DONE"
                binding.etMyName.isEnabled = true
                binding.etMyBirth.isEnabled = true
                binding.etMyEmail.isEnabled = true
                binding.etMyPhoneNumber.isEnabled = true

                binding.etMyName.setTextColor(Color.BLACK)
                binding.etMyBirth.setTextColor(Color.BLACK)
                binding.etMyEmail.setTextColor(Color.BLACK)
                binding.etMyPhoneNumber.setTextColor(Color.BLACK)
            } else {
                AlertDialog.Builder(context).apply {
                    setTitle("회원정보 수정")
                    setMessage("정보수정을 완료하시겠습니까?")
                    setPositiveButton("예") { dialog, which ->
                        val newName = binding.etMyName.text.toString()
                        val newBirth = binding.etMyBirth.text.toString()
                        val newEmail = binding.etMyEmail.text.toString()
                        val newPhoneNumber = binding.etMyPhoneNumber.text.toString()
                        binding.etMyName.isEnabled = false
                        binding.etMyBirth.isEnabled = false
                        binding.etMyEmail.isEnabled = false
                        binding.etMyPhoneNumber.isEnabled = false

                        // 상태 업데이트
                        isEditable = false
                        binding.btnMySave.text = "EDIT"
                        binding.etMyName.setTextColor(Color.parseColor("#E3E3E3"))
                        binding.etMyBirth.setTextColor(Color.parseColor("#E3E3E3"))
                        binding.etMyEmail.setTextColor(Color.parseColor("#E3E3E3"))
                        binding.etMyPhoneNumber.setTextColor(Color.parseColor("#E3E3E3"))
                    }
                    setNegativeButton("아니오", null)
                    show()
                }
            }

        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyPageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}