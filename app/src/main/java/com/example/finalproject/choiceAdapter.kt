package com.example.finalproject

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.ChoiceRowBinding

class choiceAdapter(val items:ArrayList<String>)
    : RecyclerView.Adapter<choiceAdapter.ViewHolder>(), Filterable {
    interface ItemClickListener{
        fun OnItemClick(position: Int){
        }
    }
    var itemClickListener:ItemClickListener? = null
    inner class ViewHolder(val binding: ChoiceRowBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.ingredient.setOnClickListener{
                itemClickListener?.OnItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ChoiceRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ingredient.text = items[position]
    }
    var itemsFull:ArrayList<String> = ArrayList(items.map{it.toString()})
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList: ArrayList<String> = ArrayList()
                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(itemsFull)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    for (item in itemsFull) {
                        if (item.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                items.clear()
                items.addAll(results?.values as ArrayList<String>)
                notifyDataSetChanged()
            }
        }
    }
}