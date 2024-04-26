package com.example.teamprojectchicken.activities

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ContactListAdapter
import com.example.teamprojectchicken.data.Contact
import com.example.teamprojectchicken.data.DataSource
import com.example.teamprojectchicken.databinding.AddcontactDialogBinding
import com.example.teamprojectchicken.databinding.AddnotificationDialogBinding
import com.example.teamprojectchicken.databinding.FragmentContactListBinding
import com.example.teamprojectchicken.utils.FormatUtils.VIEW_TYPE_LINEAR
import com.example.teamprojectchicken.utils.isvisible
import com.example.teamprojectchicken.viewmodels.ContactViewModel
class ContactListFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private val binding by lazy { FragmentContactListBinding.inflate(layoutInflater) }
    private val viewModel = ContactViewModel()
    val contactListAdapter by lazy {
        ContactListAdapter()
    }
    companion object {
        var list: MutableList<Contact> = DataSource.getDataSource().getContactList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactListAdapter.submitList(list)
        bind()
        addContact()
        ivSetOnClick()
        itemOnClick()
        searchContact()

        binding.btnAdd.setOnClickListener {
            showPopup(binding.btnAdd)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? FragmentActivity)?.isvisible()
    }

    private fun bind() {
        with(binding.rvListList) {
            adapter = contactListAdapter
            if (viewModel.getType() == VIEW_TYPE_LINEAR) {
                binding.ivSet.setImageResource(R.drawable.ic_list)
                layoutManager = LinearLayoutManager(requireContext())
                itemTouch.attachToRecyclerView(this)
            } else {
                layoutManager = GridLayoutManager(requireContext(),4)
                binding.ivSet.setImageResource(R.drawable.ic_grid)
            }
        }
    }

    private fun ivSetOnClick() {
        binding.ivSet.setOnClickListener {
            if (viewModel.getType() == VIEW_TYPE_LINEAR) {
                viewModel.setType()
                contactListAdapter.viewType = viewModel.getType()
                gridLayout()
            } else {
                viewModel.setType()
                linearLayout()
            }
        }
    }
    private fun linearLayout() {
        with(binding.rvListList) {
            contactListAdapter.viewType = viewModel.getType()
            adapter = contactListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            binding.ivSet.setImageResource(R.drawable.ic_list)
            itemTouch.attachToRecyclerView(this)
        }
    }
    private fun gridLayout() {
        with(binding.rvListList) {
            adapter = contactListAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
            binding.ivSet.setImageResource(R.drawable.ic_grid)
        }
    }
    private fun itemOnClick() {
        contactListAdapter.itemClick = object : ContactListAdapter.ItemClick {
            override fun onClick(view: View, position: Int, contact: Contact) {
                goToDetailContact(contact)

            }
            override fun longClick(position: Int) {
                delContact(position)
            }
        }
    }
    private fun goToDetailContact(contact: Contact){
        val fragment = ContactDetailFragment.newInstance(contact)
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(R.id.root_frag, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun delContact(position:Int) {
        var builder = AlertDialog.Builder(requireContext())
        builder.setTitle("연락처 삭제")
        builder.setMessage("해당 연락처를 삭제합니다.")

        val listener = DialogInterface.OnClickListener { p0, p1 ->
            // 다이얼로그 인터페이스 생성, 버튼 클릭시 처리 이벤트
            if (p1 == DialogInterface.BUTTON_POSITIVE ) {
                contactListAdapter.removeItem(position)
                Toast.makeText(requireContext(), "연락처 삭제 완료", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setPositiveButton("확인", listener)
        builder.setNegativeButton("취소", null)
        builder.show()
    }
    private fun searchContact() {
        binding.svListSearch.isSubmitButtonEnabled = true
        binding.svListSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }
    private fun filter(text : String?) {
        val searchText = text?.replace("-","")
        val filteredList = ArrayList<Contact>()
        for(item in list) {
            if(item.name.contains(searchText?:"") || "0${item.number}".contains(searchText?:"")) {
                filteredList.add(item)
            }
        }
        contactListAdapter.submitList(filteredList)
    }
    override fun onPause() {
        super.onPause()
        contactListAdapter.submitList(list)
    }
    // 연락처 추가 다이얼로그
    private fun addContact() {
        binding.btnAdd.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            val alertDialog: AlertDialog = builder.create()
            val binding: AddcontactDialogBinding = AddcontactDialogBinding.inflate(layoutInflater)
            alertDialog.setView(binding.root)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.dlBtnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
            binding.dlBtnRegister.setOnClickListener {
                val name = binding.dlEtName.text.toString()
                val number = binding.dlEtNumber.text.toString()
                val email = binding.dlEtEmail.text.toString()
                if (name.isNotEmpty() && number.isNotEmpty() && email.isNotEmpty()) {
                    try {
                        if (number.toInt() is Int) {
                            contactListAdapter.contactList.add(
                                Contact(
                                    name = name,
                                    number = number.toInt(),
                                    email = email,
                                    date = 20000000,
                                    userImage = R.drawable.ic_mine,
                                    heart = false,
                                    uri = null
                                )
                            )
                            contactListAdapter.notifyDataSetChanged()
                            Toast.makeText(requireContext(), "연락처를 추가했습니다.", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                        }
                    } catch (e:java.lang.NumberFormatException) {
                        Toast.makeText(requireContext(), "11자리 이하의 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } else if (name.isEmpty() || number.isEmpty() || email.isEmpty()) {
                    Toast.makeText(requireContext(), "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            alertDialog.show()
        }
    }

    val itemTouch = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val phoneNumber = DataSource.getDataSource().getContactList()[position].number
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:0$phoneNumber")
            }
            viewHolder.itemView.context.startActivity(intent)

            viewHolder.itemView.translationX = 0f // 아이템이 사라지는 마술
            contactListAdapter.notifyDataSetChanged()
        }
    })

    // 알림 추가 다이얼로그
    private fun addNotification() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val alertDialog: AlertDialog = builder.create()
        val binding: AddnotificationDialogBinding = AddnotificationDialogBinding.inflate(layoutInflater)
        alertDialog.setView(binding.root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnNotificationDlCancel.setOnClickListener {
            alertDialog.dismiss()
        }
        binding.btnNotificationDlSave.setOnClickListener {
            val name = binding.etNotificationDlName.text.toString()
            val number = binding.etNotificationDlNumber.text.toString()
            var time = 0

            // 라디오 버튼
            val radioGroup = binding.radioGroupNotificationDl
            val radioOff = binding.radioBtnNotificationDlOff
            val radio5 = binding.radioBtnNotificationDl5
            val radio15 = binding.radioBtnNotificationDl15
            val radio30 = binding.radioBtnNotificationDl30

            if (radioOff.isChecked) {
                time = 0
            } else if (radio5.isChecked) {
                time = 5
            } else if (radio15.isChecked) {
                time = 15
            } else if (radio30.isChecked) {
                time = 30
            }

            if (name.isNotEmpty() && number.isNotEmpty() && radioGroup.checkedRadioButtonId > -1) {
                try {
                    if (number.toInt() is Int) {
                        notification(name, number.toInt(), time)
                        Toast.makeText(requireContext(), "알림을 추가했습니다.", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    }
                } catch (e:java.lang.NumberFormatException) {
                    Toast.makeText(requireContext(), "11자리 이하의 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else if (name.isEmpty() || number.isEmpty() || radioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(requireContext(), "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        alertDialog.show()
    }

    // 알림 창
    fun notification(name: String, number: Int, time: Int) {
        val manager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 26 버전 이상
            val channelId="one-channel"
            val channelName="My Channel One"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                // 채널에 다양한 정보 설정
                description = "My Channel One Description"
                setShowBadge(true)
                val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(uri, audioAttributes)
                enableVibration(true)
            }
            // 채널을 NotificationManager에 등록
            manager.createNotificationChannel(channel)

            // 채널을 이용하여 builder 생성
            builder = NotificationCompat.Builder(requireContext(), channelId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!NotificationManagerCompat.from(this.requireContext()).areNotificationsEnabled()) {
                    // 알림 권한이 없다면, 사용자에게 권한 요청
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, "com.example.teamprojectchicken")
                    }
                    startActivity(intent)
                }
            }
        }else {
            // 26 버전 이하
            builder = NotificationCompat.Builder(requireContext())
        }

        val intent = Intent(this.context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        builder.run {
            setSmallIcon(R.drawable.ic_bell)
            setWhen(System.currentTimeMillis())
            setContentTitle("연락처 알림")
            setContentText("${name}에게 연락할 시간입니다.")
            addAction(R.mipmap.ic_launcher, "Action", pendingIntent)
        }
        manager.notify(11, builder.build())
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(this.context, v)
        popup.menuInflater.inflate(R.menu.popup_plus, popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.popup_addPerson -> addContact()
            R.id.popup_addNotification -> addNotification()
        }

        return item != null
    }
}