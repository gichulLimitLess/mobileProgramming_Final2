package com.example.finalproject

import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.RowBinding
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class IngerdeintAdapter(val items:ArrayList<Ingredient>)
    :RecyclerView.Adapter<IngerdeintAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: RowBinding) : RecyclerView.ViewHolder(binding.root){
        val hidden1 = binding.buydate
        val hidden2 = binding.enddate
        val hidden3 = binding.possibledate
        val hidden4 = binding.piclayout
        init {
            binding.row.setOnClickListener {
                TransitionManager.beginDelayedTransition(itemView as ViewGroup)
                if(hidden1.visibility == View.GONE){
                    hidden1.visibility = View.VISIBLE
                    hidden2.visibility = View.VISIBLE
                    hidden3.visibility = View.VISIBLE
                    hidden4.visibility = View.VISIBLE
                }else{
                    hidden1.visibility = View.GONE
                    hidden2.visibility = View.GONE
                    hidden3.visibility = View.GONE
                    hidden4.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.Iname.text = items[position].Iname
        holder.binding.IngreQuntity.text = "${items[position].Quantity.toString()}개"
        holder.binding.buyyear.text = "${items[position].BuyYear.toString()}년"
        holder.binding.buytmonth.text = "${items[position].BuyMonth.toString()}월"
        holder.binding.buyday.text = "${items[position].BuyDay.toString()}일"
        holder.binding.endyaer.text = "${items[position].EndYear.toString()}년"
        holder.binding.endmonth.text = "${items[position].EndMonth.toString()}월"
        holder.binding.endday.text = "${items[position].EndDay.toString()}일"
        val date1 = LocalDate.of(items[position].EndYear, items[position].EndMonth, items[position].EndDay)
        val date2 = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(date2, date1)
        if (daysBetween < 0){
            holder.binding.row.setBackgroundResource(R.drawable.end_date)
            holder.binding.possibleday.text = "유통기한이 ${-daysBetween}일 지났습니다."
        }else{
            holder.binding.possibleday.text = "${daysBetween}일 남았습니다"
        }
    }
}