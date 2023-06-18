package com.example.finalproject

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide.init
import com.example.finalproject.databinding.FragmentIngredientSelectBinding

class IngredientSelectFragment : DialogFragment() {
    var binding:FragmentIngredientSelectBinding? = null
    private val dbHelper: IngredientDBHelper by lazy { IngredientDBHelper(requireContext()) }
    val model:ChoiceViewModel by activityViewModels()
    val BuyYear: ArrayList<String> = arrayListOf("년", "2020", "2021", "2022", "2023")
    val BuyMonth: ArrayList<String> = arrayListOf("월", "1", "2", "3", "4", "5", "6", "7"
        , "8", "9", "10", "11", "12")
    val BuyDay: ArrayList<String> = arrayListOf("일")
    val EndYear: ArrayList<String> = arrayListOf("년", "2020", "2021", "2022", "2023")
    val EndMonth: ArrayList<String> = arrayListOf("월", "1", "2", "3", "4", "5", "6", "7"
        , "8", "9", "10", "11", "12")
    val EndDay: ArrayList<String> = arrayListOf("일")
    val Quantity: ArrayList<String> = arrayListOf("수량", "그램", "개")

    var list1:String = ""
    var list2:String = ""
    var list3:String = ""
    var list4:String = ""
    var list5:String = ""
    var list6:String = ""
    var list7:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
        for(i: Int in 1..31){
            BuyDay.add("$i")
            EndDay.add("$i")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val spinner1: Spinner = binding!!.buyyear
        val spinner2: Spinner = binding!!.buymonth
        val spinner3: Spinner = binding!!.buyday
        val spinner4: Spinner = binding!!.endyeqr
        val spinner5: Spinner = binding!!.endmonth
        val spinner6: Spinner = binding!!.endday
        val spinner7: Spinner = binding!!.gram

        val adapter1: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            BuyYear)
        val adapter2: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            BuyMonth)
        val adapter3: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            BuyDay)
        val adapter4: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            EndYear)
        val adapter5: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            EndMonth)
        val adapter6: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            EndDay)
        val adapter7: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            Quantity)

        spinner1.adapter = adapter1
        spinner2.adapter = adapter2
        spinner3.adapter = adapter3
        spinner4.adapter = adapter4
        spinner5.adapter = adapter5
        spinner6.adapter = adapter6
        spinner7.adapter = adapter7

        binding!!.buyyear.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list1 = BuyYear[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding!!.endday.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list6 = EndDay[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding!!.buymonth.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list2 = BuyMonth[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        binding!!.buyday.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list3 = BuyDay[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        binding!!.endyeqr.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list4 = EndYear[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        binding!!.endmonth.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list5 = EndMonth[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding!!.gram.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                list7 = Quantity[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding?.ingredientName?.text = model.choice.value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIngredientSelectBinding.inflate(inflater, container, false)
        init()
        return binding!!.root

    }

    private fun init(){
        binding!!.complete.setOnClickListener {
            // Check for default non-integer values in the spinner lists
            if(list1 == "년" || list2 == "월" || list3 == "일"
                || list4 == "년" || list5 == "월" || list6 == "일"
                || list7 == "수량" || binding!!.quntitiy.text.toString() == "") {
                // Display toast message
                Toast.makeText(context, "모든 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val ingredient: Ingredient = Ingredient(binding!!.ingredientName.text.toString(), binding!!.quntitiy.text.toString().toInt(),
                    list1.toInt(), list2.toInt(), list3.toInt(),
                    list4.toInt(), list5.toInt(), list6.toInt())
                // Insert Ingredient into database
                dbHelper.insertIngredient(ingredient)
                dismiss()
                activity?.finish()
            }
        }
    }


}