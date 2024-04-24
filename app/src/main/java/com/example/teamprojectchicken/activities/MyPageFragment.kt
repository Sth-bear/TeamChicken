package com.example.teamprojectchicken.activities

import android.app.AlertDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.databinding.FragmentMyPageBinding
import com.example.teamprojectchicken.utils.FormatUtils

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
    private lateinit var photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentMyPageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
        photoPickerLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                uri?.let {
                    binding.ivMyProfile.setImageURI(uri)
                }
            }

        editMyInfo()
        editProfileImage()
    }

    private fun editProfileImage() {
        binding.ivMyProfile.setOnClickListener {
            val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            photoPickerLauncher.launch(request)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        return binding.root
    }

    //정보 수정하는 부분 0424 함수명 변경 editInfo -> editMyInfo
    fun editMyInfo() {
        var isEditable = false
        binding.btnMySave.setOnClickListener {
            if (!isEditable) {
                isEditable = true
                binding.btnMySave.text = "DONE"
                enableEditTextFieldsInMyPage(true)
                changeColorAfterEnabledOpen()
            } else {
                AlertDialog.Builder(context).apply {
                    setTitle("회원정보 수정")
                    setMessage("정보수정을 완료하시겠습니까?")
                    setPositiveButton("예") { dialog, which ->
                        val newName = binding.etMyName.text.toString()
                        val newBirth = FormatUtils.checkDate(binding.etMyBirth.text.toString())
                        val newEmail = binding.etMyEmail.text.toString()
                        val newPhoneNumber = FormatUtils.checkPhoneNumber(binding.etMyPhoneNumber.text.toString())
                        val fragView = this@MyPageFragment.requireView()
                        if (newName.isBlank()) {
                            FormatUtils.showSnackBar(fragView,"이름을 입력해주세요.")
                            return@setPositiveButton
                        }
                        if (FormatUtils.checkFormat(fragView,newBirth,newPhoneNumber)) {
                            return@setPositiveButton
                        }

                        binding.apply {
                            tvMyName.text = newName
                            tvMyAge.text = FormatUtils.returnAge(newBirth)
                            etMyEmail.setText(newEmail)
                            etMyName.setText(newEmail)
                            etMyBirth.setText(FormatUtils.formatDate(newBirth))
                            etMyPhoneNumber.setText(FormatUtils.formatNumber(newPhoneNumber))
                        }

                        // 상태 업데이트
                        isEditable = false
                        enableEditTextFieldsInMyPage(false)

                        binding.btnMySave.text = "EDIT"
                        changeColorAfterEnabledClose()
                    }
                    setNegativeButton("아니요", null)
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

    // edit눌렀을 때 editext 상태 변경
    private fun enableEditTextFieldsInMyPage(isEnabled: Boolean){
        with(binding){
            binding.etMyName.isEnabled = isEnabled
            binding.etMyBirth.isEnabled = isEnabled
            binding.etMyEmail.isEnabled = isEnabled
            binding.etMyPhoneNumber.isEnabled = isEnabled
        }
    }
    // 수정 불가능 모드일 때 텍스트 컬러
    private fun changeColorAfterEnabledClose(){
        binding.etMyName.setTextColor(Color.parseColor("#E3E3E3"))
        binding.etMyBirth.setTextColor(Color.parseColor("#E3E3E3"))
        binding.etMyEmail.setTextColor(Color.parseColor("#E3E3E3"))
        binding.etMyPhoneNumber.setTextColor(Color.parseColor("#E3E3E3"))
    }
    // 수정 가능 모드일 때 텍스트 컬러
    private fun changeColorAfterEnabledOpen(){
        val openColor = R.color.myProfileUpdate.toInt()
        binding.etMyName.setTextColor(openColor)
        binding.etMyBirth.setTextColor(openColor)
        binding.etMyEmail.setTextColor(openColor)
        binding.etMyPhoneNumber.setTextColor(openColor)
    }

}
