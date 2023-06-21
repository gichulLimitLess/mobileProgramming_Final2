package com.example.finalproject

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
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
        recyclerView = binding.recycle
        adapter = IngerdeintAdapter(data)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter

        binding.recycle.apply {
            layoutManager = LinearLayoutManager(context)
        }
        if(data.isEmpty()){
            binding.checkRecipeBtn.visibility = View.GONE
            binding.adddelete.visibility = View.GONE
            binding.recycle.visibility = View.GONE
            binding.empty.visibility = View.VISIBLE
        }else{
            binding.recycle.visibility = View.VISIBLE
            binding.empty.visibility = View.GONE
            binding.checkRecipeBtn.visibility = View.VISIBLE
            binding.adddelete.visibility = View.VISIBLE
        }
        binding.add.setOnClickListener {
            val selectIntent = Intent(requireContext(), ChoiceActivity::class.java)
            startActivity(selectIntent)
        }
        binding.delete.setOnClickListener {
            dbHelper.clearAllIngredients()
            binding.checkRecipeBtn.visibility = View.GONE
            binding.adddelete.visibility = View.GONE
            binding.recycle.visibility = View.GONE
            binding.empty.visibility = View.VISIBLE
            data.clear()
        }

        binding.addingredi.setOnClickListener {
            val selectIntent = Intent(requireContext(), ChoiceActivity::class.java)
            startActivity(selectIntent)
        }

        binding.checkRecipeBtn.setOnClickListener {
            val toCheckedRecipeIntent = Intent(requireContext(), RecipeCheckActivity::class.java)
            startActivity(toCheckedRecipeIntent)
        }

        adapter.itemClickListener = object :IngerdeintAdapter.OnItemClickListener{
            override fun OnItemClick(ingredient: Ingredient) {
                val fragment = Modify_Fragment(ingredient)
                fragment.show(parentFragmentManager, "수정")
                data.clear()
                data.addAll(dbHelper.getAllIngredients())
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        data.clear()
        data.addAll(dbHelper.getAllIngredients())

        if(data.isEmpty()){
            binding.checkRecipeBtn.visibility = View.GONE
            binding.adddelete.visibility = View.GONE
            binding.recycle.visibility = View.GONE
            binding.empty.visibility = View.VISIBLE
        }else{
            binding.empty.visibility = View.GONE
            binding.checkRecipeBtn.visibility = View.VISIBLE
            binding.adddelete.visibility = View.VISIBLE
            binding.recycle.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false // 드래그 앤 드롭을 사용하지 않을 경우 false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val item = adapter.getItemAtPosition(position)
            dbHelper.deleteIngredient(item.id)
            data.removeAt(position)
            adapter.notifyItemRemoved(position)

            if(data.isEmpty()){
                binding.checkRecipeBtn.visibility = View.GONE
                binding.adddelete.visibility = View.GONE
                binding.recycle.visibility = View.GONE
                binding.empty.visibility = View.VISIBLE
            } else {
                binding.empty.visibility = View.GONE
                binding.checkRecipeBtn.visibility = View.VISIBLE
                binding.adddelete.visibility = View.VISIBLE
                binding.recycle.visibility = View.VISIBLE
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}