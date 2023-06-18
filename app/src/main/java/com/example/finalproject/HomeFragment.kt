package com.example.finalproject

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IngerdeintAdapter
    private val dbHelper: IngredientDBHelper by lazy { IngredientDBHelper(requireContext()) }
    val data:ArrayList<Ingredient> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recycle
        adapter = IngerdeintAdapter(data)
        recyclerView.adapter = adapter

        binding.recycle.apply {
            layoutManager = LinearLayoutManager(context)
        }
        if(data.isEmpty()){
            binding.button.visibility = View.GONE
            binding.adddelete.visibility = View.GONE
            binding.recycle.visibility = View.GONE
            binding.empty.visibility = View.VISIBLE
        }else{
            binding.empty.visibility = View.GONE
            binding.button.visibility = View.VISIBLE
            binding.adddelete.visibility = View.VISIBLE
            binding.recycle.visibility = View.VISIBLE
        }
        binding.add.setOnClickListener {
            val selectIntent = Intent(requireContext(), ChoiceActivity::class.java)
            startActivity(selectIntent)
        }
        binding.delete.setOnClickListener {
            dbHelper.clearAllIngredients()

            
//            이 부분에 해당 항목만 삭제할 수 있도록 디비 헬퍼에 새로 만든 함수 연결해줘야함

        }
        binding.addingredi.setOnClickListener {
            val selectIntent = Intent(requireContext(), ChoiceActivity::class.java)
            startActivity(selectIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        data.clear()
        data.addAll(dbHelper.getAllIngredients())
        if(data.isEmpty()){
            binding.button.visibility = View.GONE
            binding.adddelete.visibility = View.GONE
            binding.recycle.visibility = View.GONE
            binding.empty.visibility = View.VISIBLE
        }else{
            binding.empty.visibility = View.GONE
            binding.button.visibility = View.VISIBLE
            binding.adddelete.visibility = View.VISIBLE
            binding.recycle.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}