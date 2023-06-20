package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalproject.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {

    //ViewBinding 기법을 사용하기 위해 binding 객체 하나 생성
    lateinit var binding: FragmentMypageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMypageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayout()

    }

    private fun initLayout() {
        //즐겨 찾는 레시피 버튼을 눌렀을 때

        //나만의 레시피 버튼을 눌렀을 때
        binding.recipeOfMine.setOnClickListener {
            val intent = Intent(requireContext(), RecipeByMeActivity::class.java)
            startActivity(intent) //나만의 레시피 Activity로 간다
        }

        binding.favoriteRecipeName.setOnClickListener {
            val intent = Intent(requireContext(), LikedRecipeActivity::class.java)
            startActivity(intent) //즐겨찾는 레시피 Activity로 간다
        }
    }
}