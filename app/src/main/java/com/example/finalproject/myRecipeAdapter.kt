package com.example.finalproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.RecipeRowInmyrecipeBinding

class myRecipeAdapter(var recipe_items: ArrayList<recipeData>, var ingredients: ArrayList<Ingredient>)
    : RecyclerView.Adapter<myRecipeAdapter.RecipeViewHolder>(){

    //Click 이벤트 처리하기 위한 interface
    interface OnItemClickListener {
        fun OnItemClick(item: recipeData, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class RecipeViewHolder(val binding: RecipeRowInmyrecipeBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            //'레시피'라는 고정된 텍스트를 클릭했을 때 이벤트를 발생시킨다
            binding.modifyMyRecipeBtn.setOnClickListener {
                //bindingAdapterPosition은 현재 ViewHolder 클래스가 존재하는 어댑터에서의 position을 반환
                //RecyclerView에 하나의 어댑터만 사용하고 있다면 이 메서드를 사용해서 아이템의 position을 가져올 수 있다
                itemClickListener?.OnItemClick(recipe_items[adapterPosition], adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myRecipeAdapter.RecipeViewHolder {
        val view = RecipeRowInmyrecipeBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)

        //생성한 viewHolder를 반환한다
        return RecipeViewHolder(view)
    }

    //들어온 정보들을 가지고 맵핑 해주는 건 여기서 해준다
    override fun onBindViewHolder(holder: myRecipeAdapter.RecipeViewHolder, position: Int) {
        holder.binding.allStuffCount.text = recipe_items[position].recipe_stuff.count().toString()
        holder.binding.recipeName.text = recipe_items[position].recipe_name

        var count = 0
        var i = 0

        //현재 필요한 양을 충족하는 재료의 개수는 계산 해서 넣어 주어야 한다 (일부 만이라도 포함된 경우)
        //계산해줄 것이다
        for(recipes_stuff in recipe_items[position].recipe_stuff)
        {
            for(ingredient in ingredients)
            {
                if(ingredient.Iname == recipes_stuff && ingredient.Quantity <= recipe_items[position].recipe_stuff_count[i])
                {
                    count++
                    break;
                }
                i++
            }
            i = 0
        }

        holder.binding.havingIngredientCount.text = count.toString()
    }

    override fun getItemCount(): Int {
        return recipe_items.size
    }
}