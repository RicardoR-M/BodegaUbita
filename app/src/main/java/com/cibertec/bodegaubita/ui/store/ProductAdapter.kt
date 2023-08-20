package com.cibertec.bodegaubita.ui.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.bodegaubita.R
import com.cibertec.bodegaubita.common.Utils.Companion.numToSol
import com.cibertec.bodegaubita.databinding.ItemProductBinding
import com.cibertec.bodegaubita.model.Product
import com.squareup.picasso.Picasso

class ProductAdapter(
    var products: List<Product> = emptyList(),
    val onItemAdd: (Product) -> Unit,
    val onItemDetail: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemProductBinding = ItemProductBinding.bind(itemView)

        fun bind(product: Product) = with(binding) {
            tvProductName.text = product.name
            tvProductQuantity.text = product.unit
            tvProductPrice.text = numToSol(product.price)

            if (product.image.isNotEmpty()) {
                Picasso.get()
                    .load(product.image)
                    .into(ivProduct)
            }

            btnAdd.setOnClickListener {
                onItemAdd(product)
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
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
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