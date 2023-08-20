package com.cibertec.bodegaubita.ui.account.orders

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cibertec.bodegaubita.databinding.ActivityOrderDetailBinding
import com.cibertec.bodegaubita.model.Order
import com.cibertec.bodegaubita.ui.cart.CartAdapter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private var cartAdapter = CartAdapter(flagIcons = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT-5")

        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val order = intent.getParcelableExtra<Order>("order") as Order
        binding.rvProducts.adapter = cartAdapter
        binding.tvOrderId.text = order.id
        binding.tvDate.text = sdf.format(order.getDate().toDate())

        if (order.delivered) {
            binding.tvOrderStatus.text = "Entregado"
            binding.tvOrderStatus.setBackgroundColor(Color.GREEN)
            binding.tvOrderStatus.setTextColor(Color.BLACK)
        } else {
            binding.tvOrderStatus.text = "Pendiente"
            binding.tvOrderStatus.setBackgroundColor(Color.RED)
            binding.tvOrderStatus.setTextColor(Color.WHITE)
        }

        init()
        events()
    }

    override fun onStart() {
        super.onStart()
        val order = intent.getParcelableExtra<Order>("order") as Order
        cartAdapter.updateList(order.products)
    }

    private fun init() {
        return
    }
    private fun events() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}