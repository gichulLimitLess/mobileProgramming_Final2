package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.finalproject.databinding.FragmentIngredientSelectBinding
import com.example.finalproject.databinding.FragmentModifyBinding

class Modify_Fragment(ingredient: Ingredient) : DialogFragment() {
    var binding:FragmentModifyBinding? = null
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
    val Unit: ArrayList<String> = arrayListOf("수량", "그램", "개")

    var list0:String = ingredient.Iname
    var list1:String = ingredient.BuyYear.toString()
    var list2:String = ingredient.BuyMonth.toString()
    var list3:String = ingredient.BuyDay.toString()
    var list4:String = ingredient.EndYear.toString()
    var list5:String = ingredient.EndMonth.toString()
    var list6:String = ingredient.EndDay.toString()
    var list7:String = ingredient.Unit
    var list8:String = ingredient.Quantity.toString()

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
            Unit)

        spinner1.adapter = adapter1
        spinner2.adapter = adapter2
        spinner3.adapter = adapter3
        spinner4.adapter = adapter4
        spinner5.adapter = adapter5
        spinner6.adapter = adapter6
        spinner7.adapter = adapter7

        val BY = BuyYear.indexOf(list1)
        spinner1.setSelection(BY)
        val BM = BuyMonth.indexOf(list2)
        spinner2.setSelection(BM)
        val BD = BuyDay.indexOf(list3)
        spinner3.setSelection(BD)
        val EY = EndYear.indexOf(list4)
        spinner4.setSelection(EY)
        val EM = EndMonth.indexOf(list5)
        spinner5.setSelection(EM)
        val ED = EndDay.indexOf(list6)
        spinner6.setSelection(ED)
        val UN = Unit.indexOf(list7)
        spinner7.setSelection(UN)

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
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list7 = Unit[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModifyBinding.inflate(inflater, container, false)
        init()
        binding!!.ingredientName.text = list0
        binding!!.quntitiy.setText(list8)
        return binding!!.root
    }

    private fun init(){
        binding!!.completeModify.setOnClickListener {
            // Check for default non-integer values in the spinner lists
            if(list1 == "년" || list2 == "월" || list3 == "일"
                || list4 == "년" || list5 == "월" || list6 == "일"
                || list7 == "수량" || binding!!.quntitiy.text.toString() == "") {
                // Display toast message
                Toast.makeText(context, "모든 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val ingredient: Ingredient = Ingredient(
                    0,
                    binding!!.ingredientName.text.toString(),
                    binding!!.quntitiy.text.toString().toInt(),
                    list1.toInt(), list2.toInt(), list3.toInt(),
                    list4.toInt(), list5.toInt(), list6.toInt(),
                    list7
                )
                dbHelper.updateIngredient(ingredient)
                dismiss()
            }
        }
    }
}