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
    private var editMode = false
    private var profileImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
        setupActivityResultLauncher()
        setupListeners()
    }

    private fun setupActivityResultLauncher() {
        photoPickerLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
                uri?.let {
                    binding.ivMyProfile.setImageURI(uri)
                }
            }
    }

    private fun setupListeners() {
        binding.ivMyProfile.setOnClickListener {
            if (editMode){
                launchPhotoPicker()
            }else{
                showProfilePhotoInFullScreen()
            }

        }
        binding.btnMySave.setOnClickListener {
            handleSaveButton()
        }
    }

    private fun showProfilePhotoInFullScreen() {
        binding.ivMyProfileBig.visibility = android.view.View.VISIBLE
        binding.ivMyProfileBig.setOnClickListener {
            binding.ivMyProfileBig.visibility = android.view.View.INVISIBLE
        }
    }

    private fun launchPhotoPicker() {
        val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        photoPickerLauncher.launch(request)
    }

    private fun handleSaveButton() {
        if (isEditable()) {
            showConfirmationDialog()
        } else {
            toggleEditMode()
        }
    }

    private fun isEditable(): Boolean {
        return binding.btnMySave.text.toString() == "DONE"
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(context).apply {
            setTitle("회원정보 수정")
            setMessage("정보수정을 완료하시겠습니까?")
            setPositiveButton("예") { dialog, which ->
               if(handleUserInfoChanges()){
                toggleEditMode()}
            }
            setNegativeButton("아니오", null)
            show()
        }
    }

    private fun toggleEditMode() {
        editMode = !editMode
        val newButtonText = if (isEditable()) "EDIT" else "DONE"
        binding.btnMySave.text = newButtonText
        enableEditTextFieldsInMyPage(isEditable())
        if (!isEditable()){
            changeColorAfterEnabledClose()
        } else {
            changeColorAfterEnabledOpen()
        }
    }

    private fun handleUserInfoChanges() : Boolean {
        val newName = binding.etMyName.text.toString()
        val newBirth = FormatUtils.checkDate(binding.etMyBirth.text.toString())
        val newEmail = binding.etMyEmail.text.toString()
        val newPhoneNumber = FormatUtils.checkPhoneNumber(binding.etMyPhoneNumber.text.toString())

        val fragView = requireView()
        if (newName.isBlank()) {
            FormatUtils.showToast(fragView.context,"이름을 입력해주세요.")
            return false
        }
        if (FormatUtils.checkFormat(fragView.context,newBirth,newPhoneNumber)) {
            return false
        }

        binding.apply {
            tvMyName.text = newName
            tvMyAge.text = FormatUtils.returnAge(newBirth)
            etMyEmail.setText(newEmail)
            etMyName.setText(newName)
            etMyBirth.setText(FormatUtils.formatDate(newBirth))
            etMyPhoneNumber.setText(FormatUtils.formatNumber(newPhoneNumber))
        }
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
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
