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
import androidx.fragment.app.Fragment
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.databinding.FragmentMyPageBinding
import com.example.teamprojectchicken.utils.FormatUtils


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
        setupActivityResultLauncher()
        editMyInfo()
        editProfileImage()
    }

    private fun setupActivityResultLauncher() {
        photoPickerLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                uri?.let {
                    binding.ivMyProfile.setImageURI(uri)
                }
            }
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
                            etMyName.setText(newName)
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

    // edit눌렀을 때 editext 상태 변경
    private fun enableEditTextFieldsInMyPage(isEnabled: Boolean){
        with(binding){
            etMyName.isEnabled = isEnabled
            etMyBirth.isEnabled = isEnabled
            etMyEmail.isEnabled = isEnabled
            etMyPhoneNumber.isEnabled = isEnabled
        }
    }
    // 수정 불가능 모드일 때 텍스트 컬러
    private fun changeColorAfterEnabledClose(){
        val closeColor = Color.parseColor("#E3E3E3")
        setEditTextColor(closeColor)
    }

    // 수정 가능 모드일 때 텍스트 컬러
    private fun changeColorAfterEnabledOpen(){
        val openColor = resources.getColor(R.color.myProfileUpdate)
        setEditTextColor(openColor)
    }

    private fun setEditTextColor(color: Int) {
        binding.etMyName.setTextColor(color)
        binding.etMyBirth.setTextColor(color)
        binding.etMyEmail.setTextColor(color)
        binding.etMyPhoneNumber.setTextColor(color)
    }

    companion object {
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
