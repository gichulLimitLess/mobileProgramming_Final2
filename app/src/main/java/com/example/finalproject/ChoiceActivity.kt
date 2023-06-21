package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.ActivityChoiceBinding
import java.util.Scanner

class ChoiceActivity : AppCompatActivity() {
    lateinit var binding: ActivityChoiceBinding
    val Api_Ingredient:ArrayList<String> = ArrayList() //Api에서 재료 긁어오고 저장할 배열
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: choiceAdapter
    val model:ChoiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChoiceBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        init()
    }

    fun init(){
        val scan = Scanner(resources.openRawResource(R.raw.api))
        while(scan.hasNextLine()) {
            val tmp = scan.nextLine().toString()
            Api_Ingredient.add(tmp)
        }

        recyclerView = binding.recycle
        adapter = choiceAdapter(Api_Ingredient)
        adapter.itemClickListener = object:choiceAdapter.ItemClickListener{
            override fun OnItemClick(position: Int) {
                model.choice.value = Api_Ingredient[position]
                IngredientSelectFragment().show(
                    supportFragmentManager, "ingredient"
                )
            }
        }
        recyclerView.adapter = adapter
        binding.recycle.apply {
            layoutManager = LinearLayoutManager(context)
        }

        binding.SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색어를 제출할 때 호출됩니다.
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                // 검색어가 변경될 때 호출됩니다.
                newText?.let {
                    // 필터링된 결과를 업데이트합니다.
                    adapter.filter.filter(it)
                }
                return true
            }
        })
    }
}