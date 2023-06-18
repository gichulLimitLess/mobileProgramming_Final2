package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import com.example.finalproject.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    val homeFragment = HomeFragment()
    val recipeFragment = RecipeFragment()
    val myPageFragment = MypageFragment()

    //아래 tabLayout에 들어갈 textArr와 IconArr 추가
    val textArr = arrayListOf("나의 냉장고", "전체 레시피", "마이 페이지")
    val IconArr = arrayListOf<Int>(R.drawable.refrig, R.drawable.recip, R.drawable.person)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initLayout()
        //앱 상단에 기본적으로 표시되는 작업 표시줄을 가려주기 위한 구문
        supportActionBar?.hide()
    }

    //viewPager와 tabLayout을 연결하는 구문
    fun initLayout()
    {
        binding.viewPager.adapter = MyViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
                tab, pos ->
            tab.text = textArr[pos]
            tab.setIcon(IconArr[pos])
            //위처럼 끝내면 안된다. attach 작업이 추가로 필요
        }.attach()
    }
}