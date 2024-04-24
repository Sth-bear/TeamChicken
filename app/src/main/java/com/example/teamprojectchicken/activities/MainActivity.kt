package com.example.teamprojectchicken.activities

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.adapters.ViewPagerAdapter
import com.example.teamprojectchicken.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
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
        val rootFragment = RootFragment()
        val unSelected: Int = ContextCompat.getColor(this, R.color.colorUnSelect)
        val selected: Int = ContextCompat.getColor(this, R.color.colorSelect)
        val tabLayout = binding.tlMainTapLayout

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(selected, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(unSelected, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })

        val viewPager = binding.vpMain
        val adapter = ViewPagerAdapter(this)
        binding.vpMain.adapter = adapter

        adapter.addFragment(rootFragment)
        adapter.addFragment(HeartFragment())
        adapter.addFragment(MyPageFragment())

        tabLayout.getTabAt(0)

//         탭레이아웃과 뷰페이저의 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.icon = getDrawable(R.drawable.ic_home)
                1 -> tab.icon = getDrawable(R.drawable.ic_heart)
                2 -> tab.icon = getDrawable(R.drawable.ic_mine)
            }
        }.attach()

        // 시작 탭
        tabLayout.getTabAt(0)?.select()
    }


    override fun onBackPressed() {
        if (binding.vpMain.currentItem != 0) {
            binding.vpMain.currentItem = 0
        }else{
            super.onBackPressed()
        }
    }


}