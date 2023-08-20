package com.cibertec.bodegaubita.ui.admin.order

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cibertec.bodegaubita.databinding.ActivityOrderAdminDetailBinding
import com.cibertec.bodegaubita.model.Order
import com.cibertec.bodegaubita.ui.cart.CartAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class OrderAdminDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderAdminDetailBinding
    private var cartAdapter = CartAdapter(flagIcons = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT-5")

        binding = ActivityOrderAdminDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val order = intent.getParcelableExtra<Order>("order") as Order
        binding.rvProducts.adapter = cartAdapter
        binding.tvOrderId.text = order.id
        binding.tvDate.text = sdf.format(order.getDate().toDate())

        if (order.delivered) {
            binding.btnDelivered.isEnabled = false
            binding.btnDelivered.alpha = 0.5f
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
        binding.btnDelivered.setOnClickListener {
            // updates the order status to delivered in firebase
            GlobalScope.launch(Dispatchers.Main) {
                val order = intent.getParcelableExtra<Order>("order") as Order
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    withContext(Dispatchers.IO) {
                        val db = Firebase.firestore
                        db.collection("Order").document(order.id).update("delivered", true).await()
                    }
                    Toast.makeText(this@OrderAdminDetailActivity, "Orden entregada!", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Log.e("OrderAdminDetail", "Error updating order status: ${e.message}")
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }


        }
    }
}