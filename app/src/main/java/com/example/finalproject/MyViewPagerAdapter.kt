package com.example.finalproject

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

//ViewPager를 사용하기 위해 MyViewPagerAdapter Class 구현
class MyViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    //Item 갯수 반환
    override fun getItemCount(): Int {
        return 3
    }

    //Fragment 정보 반환
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return HomeFragment()
            1 -> return RecipeFragment()
            2 -> return MypageFragment()
            else -> return HomeFragment()
        }
    }
}