package com.cibertec.bodegaubita.ui.admin.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.bodegaubita.databinding.FragmentOrderAdminBinding
import com.cibertec.bodegaubita.model.Order
import com.cibertec.bodegaubita.ui.account.orders.OrdersAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OrderAdminFragment : Fragment() {

    private lateinit var binding: FragmentOrderAdminBinding
    private var ordersAdapter = OrdersAdapter(
        onItemSelected = { order-> onItemSelected(order)}
    )

    private fun onItemSelected(order: Order) {
        val bundle = Bundle().apply {
            putParcelable("order", order)
        }

        val intent = Intent(requireContext(), OrderAdminDetailActivity::class.java).apply { putExtras(bundle) }
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOrderAdminBinding.inflate(inflater, container, false)

        init()
        events()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getOrders()
    }

    private fun init() {
        binding.rvOrders.adapter = ordersAdapter
    }

    private fun events() {
        return
    }

    private fun getOrders() {
        val collectionOrder = Firebase.firestore.collection("Order")
        GlobalScope.launch(Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val orders = withContext(Dispatchers.IO) {
                    collectionOrder.get().await().toObjects(Order::class.java)
                }
                ordersAdapter.updateList(orders)
            } catch (ex: Exception) {
                Log.e("OrderAdminActivity", "Error al obtener las ordenes", ex)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

}