package com.example.teamprojectchicken.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ViewPagerAdapter
import com.example.teamprojectchicken.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setViewPager()
    }

    private fun setViewPager() {
        val tabLayout = binding.tlMainTapLayout
        val viewPager = binding.vpMain
        val adapter = ViewPagerAdapter(this)
        binding.vpMain.adapter= adapter

        adapter.addFragment(ContactListFragment())
        adapter.addFragment(MyPageFragment())

        // 탭레이아웃 아이콘 추가
        val iconList = ArrayList<Drawable?>()
        iconList.add(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground))
        iconList.add(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground))

        // 탭레이아웃과 뷰페이저의 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon = iconList[position]
        }.attach()

        // 시작 탭
        tabLayout.getTabAt(0)?.select()
    }
}