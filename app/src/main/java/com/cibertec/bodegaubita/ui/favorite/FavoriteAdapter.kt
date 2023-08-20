package com.cibertec.bodegaubita.ui.favorite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.bodegaubita.R
import com.cibertec.bodegaubita.common.Utils.Companion.numToSol
import com.cibertec.bodegaubita.databinding.ItemProductFavoriteBinding
import com.cibertec.bodegaubita.model.Product
import com.squareup.picasso.Picasso

class FavoriteAdapter(
    var products: List<Product> = emptyList(),
    val onItemDetail: (Product) -> Unit
): RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemProductFavoriteBinding = ItemProductFavoriteBinding.bind(itemView)

        fun bind(product: Product) = with(binding) {
            tvProductName.text = product.name
            tvProductUnit.text = product.unit
            tvProductPrice.text = numToSol(product.price)

            if (product.image.isNotEmpty()) {
                Picasso.get()
                    .load(product.image)
                    .into(ivProduct)
            }

            root.setOnClickListener {
                onItemDetail(product)
            }
        }
    }

    fun updateList(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_product_favorite, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

}