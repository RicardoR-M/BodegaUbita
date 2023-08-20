package com.cibertec.bodegaubita.ui.account.orders

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.cibertec.bodegaubita.databinding.ActivityOrdersBinding
import com.cibertec.bodegaubita.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding
    private lateinit var auth: FirebaseAuth

    private var ordersAdapter = OrdersAdapter(
        onItemSelected = { order ->
            onItemSelected(order)
        }
    )

    private fun onItemSelected(order: Order) {
        val bundle = Bundle().apply {
            putParcelable("order", order)
        }

        val intent = Intent(this, OrderDetailActivity::class.java).apply { putExtras(bundle) }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        init()
        events()
    }

    override fun onStart() {
        super.onStart()
        getOrders()
    }

    private fun init(){
        binding.rvOrders.adapter = ordersAdapter
        auth = Firebase.auth
    }

    private fun events(){
        binding.btnback.setOnClickListener {
            finish()
        }
    }

    private fun getOrders(){
        val user = auth.currentUser
        if (user != null) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    val orders = withContext(Dispatchers.IO) {
                        Firebase.firestore.collection("Order")
                            .whereEqualTo("userUid", user.uid)
                            .get()
                            .await()
                            .toObjects(Order::class.java)
                    }
                    ordersAdapter.updateList(orders)
                } catch (ex: Exception) {
                    Log.e("OrdersActivity", "Error al obtener las ordenes", ex)
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}