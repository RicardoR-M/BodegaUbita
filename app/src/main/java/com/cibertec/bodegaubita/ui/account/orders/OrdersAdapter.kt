package com.cibertec.bodegaubita.ui.account.orders

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.bodegaubita.R
import com.cibertec.bodegaubita.databinding.ItemOrderBinding
import com.cibertec.bodegaubita.model.Order
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrdersAdapter (
    var orders: List<Order> = emptyList(),
    val onItemSelected: (Order) -> Unit = {}
) :RecyclerView.Adapter<OrdersAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemOrderBinding = ItemOrderBinding.bind(itemView)

        fun bind(order: Order) = with(binding) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("GMT-5")

            tvOrderId.text = order.id
            tvDate.text = sdf.format(order.getDate().toDate())
            if (order.delivered){
                tvOrderStatus.text =  "Entregado"
                tvOrderStatus.setBackgroundColor(Color.GREEN)
                tvOrderStatus.setTextColor(Color.BLACK)
            } else {
                tvOrderStatus.text =  "Pendiente"
                tvOrderStatus.setBackgroundColor(Color.RED)
                tvOrderStatus.setTextColor(Color.WHITE)
            }

            binding.root.setOnClickListener {
                onItemSelected(order)
            }
        }
    }

    fun updateList(orders: List<Order>) {
        this.orders = orders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }


}