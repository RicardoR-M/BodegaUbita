package com.cibertec.bodegaubita.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.bodegaubita.R
import com.cibertec.bodegaubita.common.Utils.Companion.numToSol
import com.cibertec.bodegaubita.databinding.ItemProductCartBinding
import com.cibertec.bodegaubita.model.OrderItem
import com.squareup.picasso.Picasso

class CartAdapter(
    var cartList: List<OrderItem> = emptyList(),
    val onItemDelete: (OrderItem) -> Unit = {},
    val onItemIncrease: (String) -> Unit = {},
    val onItemDecrease: (String) -> Unit = {},
    var flagIcons: Boolean = true
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemProductCartBinding = ItemProductCartBinding.bind(itemView)

        fun bind(orderItem: OrderItem) = with(binding) {
            tvProductName.text = orderItem.name
            tvProductUnit.text = orderItem.unit
            tvProductPrice.text = numToSol(orderItem.price * orderItem.quantity)
            tvProductQuantity.text = orderItem.quantity.toString()

            if (!flagIcons) {
                btnDecrease.visibility = View.GONE
                btnIncrease.visibility = View.GONE
                btnRemoveItem.visibility = View.GONE
            }

            if (orderItem.image.isNotEmpty()) {
                Picasso.get()
                    .load(orderItem.image)
                    .into(ivProduct)
            }

            btnRemoveItem.setOnClickListener {
                onItemDelete(orderItem)
            }

            btnDecrease.setOnClickListener {
                onItemDecrease(orderItem.productId)
                tvProductQuantity.text = orderItem.quantity.toString()
                tvProductPrice.text = numToSol(orderItem.price * orderItem.quantity)
            }

            btnIncrease.setOnClickListener {
                onItemIncrease(orderItem.productId)
                tvProductQuantity.text = orderItem.quantity.toString()
                tvProductPrice.text = numToSol(orderItem.price * orderItem.quantity)
            }
        }
    }

    fun updateList(cartList: List<OrderItem>) {
        this.cartList = cartList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_product_cart, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartList[position]
        holder.bind(cartItem)
    }
}