package com.example.teamprojectchicken.activities

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ViewPagerAdapter
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
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private val binding get() = _binding!!

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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

    private fun permissionLauncher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
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
        return binding.root
    }

    //ContactListFragment에서 받아온 값 출력
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = binding.ivDetailProfile
        binding.ivDetailProfile.setOnClickListener {
            permissionLauncher()
        }

        var heart = contact?.heart

        // 라이브데이터에 contact.heart 저장
        if (heart != null) {
            viewModel.setData(heart)
        }
        contact?.let { contact ->
            binding.apply {
                etDetailName.setText(contact.name)
                etDetailPhoneNumber.setText(FormatUtils.formatNumber(contact.number))
                etDetailBirth.setText(FormatUtils.formatDate(contact.date))
                etDetailEmail.setText(contact.email)
                if (contact.uri == null) {
                    ivDetailProfile.setImageResource(contact.userImage)
                } else {
                    ivDetailProfile.setImageURI(contact.uri)
                }
                tvDetailAge.text = FormatUtils.returnAge(contact.date)
                tvDetailName.text = contact.name

                // 클릭시 뷰모델의 라이브데이터에 Not을 입력
                btnDetailHeart.setOnClickListener {
                    heart = heart?.not()
                    heart?.let { it1 -> viewModel.setData(it1) }
                    contact.heart = heart == true
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
                tvDetailAge.text = FormatUtils.returnAge(contact.date)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
                parentFragment?.onStart()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onPause() {
        super.onPause()
        parentFragmentManager.popBackStack()
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

    //디테일 페이지 정보 수정
    fun editUserInfo() {
        var isEditable = false
        binding.btnDetailSave.setOnClickListener {
            if (!isEditable) {
                isEditable = true
                binding.btnDetailSave.text = "DONE"
                enableEditTextFields(true)
            } else {
                AlertDialog.Builder(context).apply {
                    setTitle("연락처 수정")
                    setMessage("정보수정을 완료하시겠습니까?")
                    setPositiveButton("예") { dialog, which ->
                        val name = binding.etDetailName.text.toString()
                        val date = FormatUtils.checkDate(binding.etDetailBirth.text.toString())
                        val email = binding.etDetailEmail.text.toString()
                        val phoneNumber = FormatUtils.checkPhoneNumber(binding.etDetailPhoneNumber.text.toString())
                        val fragView = this@ContactDetailFragment.requireView()
                        if (name.isBlank()) {
                            FormatUtils.showToast(context,"이름을 입력해주세요.")
                            return@setPositiveButton
                        }
                        if (FormatUtils.checkFormat(context,date,phoneNumber)) {
                            return@setPositiveButton
                        }

                        contact?.name = name
                        contact?.date = date
                        contact?.email = email
                        contact?.number = phoneNumber

                        binding.apply {
                            tvDetailAge.text = FormatUtils.returnAge(date)
                            tvDetailName.text = name
                            etDetailBirth.setText(FormatUtils.formatDate(date))
                            etDetailEmail.setText(email)
                            etDetailName.setText(name)
                            etDetailPhoneNumber.setText(FormatUtils.formatNumber(phoneNumber))
                        }

                        isEditable = false
                        enableEditTextFields(false)
                        binding.btnDetailSave.text = "EDIT"

                    }
                    setNegativeButton("아니요", null)
                    show()

                }
            }
        }
        Log.d("log_변경전","$contact")
    }


    //editText enable 관리 함수
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

