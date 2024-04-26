package com.example.teamprojectchicken.activities

import  android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.databinding.DeletecontactDialogBinding
import com.example.teamprojectchicken.databinding.EditcontactDetailDialogBinding
import com.example.teamprojectchicken.databinding.FragmentContactDetailBinding
import com.example.teamprojectchicken.utils.FormatUtils
import com.example.teamprojectchicken.viewmodels.ContactViewModel
import com.example.teamprojectchicken.utils.visible

private const val ARG_CONTACT = "contact"

class ContactDetailFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback
    private var viewModel = ContactViewModel()
    private var contact: Contact? = null
    private var _binding: FragmentContactDetailBinding? = null
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private var editMode = false


    private val binding get() = _binding!!

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickImageLauncher.launch(gallery)
            }
        }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                imageUri = it.data?.data
                imageView.setImageURI(imageUri)
                contact?.uri = imageUri
            }
        }



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
        editUserInfo()
       (activity as? FragmentActivity)?.visible()
        heart()
        return binding.root
    }

    //ContactListFragment에서 받아온 값 출력
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayInfo()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
                //parentFragment?.onStart()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
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

    private fun setProfileImage(contact: Contact) {
        if (contact.uri == null) {
            binding.ivDetailProfile.setImageResource(contact.userImage)
        } else {
            binding.ivDetailProfile.setImageURI(contact.uri)
        }
    }

    //초기 유저 정보 디스플레이
    private fun displayInfo(){
        contact?.let { contact ->
            binding.apply {
                etDetailName.setText(contact.name)
                etDetailPhoneNumber.setText(FormatUtils.formatNumber(contact.number))
                etDetailBirth.setText(FormatUtils.formatDate(contact.date))
                etDetailEmail.setText(contact.email)
                setProfileImage(contact)
                tvDetailAge.text = FormatUtils.returnAge(contact.date)
                tvDetailName.text = contact.name
            }
        }
    }

    //하트 기능
    private fun heart() {
        var heart = contact?.heart
        // 라이브데이터에 contact.heart 저장
        if (heart != null) {
            viewModel.setData(heart)
        }
        contact?.let {
            binding.apply {
                btnDetailHeart.setOnClickListener {
                    heart = heart?.not()
                    heart?.let { it1 -> viewModel.setData(it1) }
                    contact!!.heart = heart == true
                }
                val observer = Observer<Boolean> {
                    if (it) {
                        btnDetailHeart.setImageResource(R.drawable.ic_heart_filled)
                    } else {
                        btnDetailHeart.setImageResource(R.drawable.ic_heart)
                    }
                }
                viewModel.liveData.observe(viewLifecycleOwner, observer)
                tvDetailAge.text = FormatUtils.returnAge(contact!!.date)
            }
        }
    }
    //권한 요청
    private fun permissionLauncher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    //유저 정보 수정
    fun editUserInfo() {
        var isEditable = false
        binding.btnDetailSave.setOnClickListener {
            if (!isEditable){
                imageView = binding.ivDetailProfile
                binding.ivDetailProfile.setOnClickListener {
                    permissionLauncher()
                }
            }
            isEditable = !isEditable
            updateEditMode(isEditable)

        }
    }


    //수정모드 들어가는 부분 ~~confirmEdit까지
    private fun updateEditMode(isEditable: Boolean) {
        if (isEditable) {
            enterEditMode()
        } else {
            confirmEdit()
        }
        editMode = false
    }

    private fun enterEditMode() {
        binding.btnDetailSave.text = "DONE"
        enableEditTextFields(true)
    }

    private fun confirmEdit() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val alertDialog: AlertDialog = builder.create()
        val binding: EditcontactDetailDialogBinding = EditcontactDetailDialogBinding.inflate(layoutInflater)
        alertDialog.setView(binding.root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dlBtnEditCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        binding.dlBtnEditConfirm.setOnClickListener {
            saveUserInfo()
            Toast.makeText(requireContext(), "연락처가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }
        alertDialog.show()

    }

    //
    private fun saveUserInfo() {
        val name = binding.etDetailName.text.toString()
        val date = FormatUtils.checkDate(binding.etDetailBirth.text.toString())
        val email = binding.etDetailEmail.text.toString()
        val phoneNumber = FormatUtils.checkPhoneNumber(binding.etDetailPhoneNumber.text.toString())

        if (!validateUserInfo(name, date, phoneNumber)) return

        updateContactInfo(name, date, email, phoneNumber)
        updateUI(name, date, phoneNumber, email)

        binding.btnDetailSave.text = "EDIT"
        enableEditTextFields(false)
    }

    private fun validateUserInfo(name: String, date: Int, phoneNumber: Int): Boolean {
        if (name.isBlank()) {
            context?.let { FormatUtils.showToast(it, "이름을 입력해주세요.") }
            return false
        }
        if (context?.let { FormatUtils.checkFormat(it, date, phoneNumber) } == true) {
            return false
        }
        return true
    }

    private fun updateContactInfo(name: String, date: Int, email: String, phoneNumber: Int) {
        contact?.apply {
            this.name = name
            this.date = date
            this.email = email
            this.number = phoneNumber
        }
    }

    private fun updateUI(name: String, date: Int, phoneNumber: Int, email: String) {
        binding.apply {
            tvDetailAge.text = FormatUtils.returnAge(date)
            tvDetailName.text = name
            etDetailBirth.setText(FormatUtils.formatDate(date))
            etDetailEmail.setText(email)
            etDetailName.setText(name)
            etDetailPhoneNumber.setText(FormatUtils.formatNumber(phoneNumber))
        }
    }


    private fun enableEditTextFields(isEnabled: Boolean) {
        with(binding) {
            etDetailName.isEnabled = isEnabled
            etDetailBirth.isEnabled = isEnabled
            etDetailEmail.isEnabled = isEnabled
            etDetailPhoneNumber.isEnabled = isEnabled
            ivDetailProfile.isEnabled = isEnabled
        }
    }


}

    //editText enable 관리 함수

