package com.cibertec.bodegaubita.ui.explore

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.bodegaubita.R
import com.cibertec.bodegaubita.databinding.ItemCategoryBinding
import com.cibertec.bodegaubita.model.Category
import com.squareup.picasso.Picasso

class CategoryAdapter (
    var categories: List<Category> = emptyList(),
    val onItemSelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemCategoryBinding = ItemCategoryBinding.bind(itemView)

        @SuppressLint("Range")
        fun bind(category: Category) = with(binding) {
            tvCategoryName.text = category.name
            clCategory.setBackgroundColor(Color.parseColor(category.background))

            if (category.image.isNotEmpty()){
                Picasso.get()
                    .load(category.image)
                    .into(ivCategory)
            }

            root.setOnClickListener {
                onItemSelected(category)
            }
        }
    }

    fun updateList(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }
}