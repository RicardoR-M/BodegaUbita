package com.cibertec.bodegaubita.ui.admin.product

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.bodegaubita.databinding.FragmentProductAdminBinding
import com.cibertec.bodegaubita.model.Product
import com.cibertec.bodegaubita.ui.store.ProductAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ProductAdminFragment : Fragment() {

    private lateinit var binding: FragmentProductAdminBinding
    private var productAdapter: ProductAdapter = ProductAdapter(
        onItemAdd = { } ,
        onItemDetail = { product -> onItemDetail(product) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProductAdminBinding.inflate(inflater, container, false)

        init()
        events()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getProducts()
    }

    private fun events() {
        binding.btnAddProduct.setOnClickListener {
            val intent = Intent(requireContext(), ProductAdminDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun init() {
        binding.rvProducts.adapter = productAdapter
    }

    private fun onItemDetail(product: Product) {
        val bundle = Bundle().apply {
            putSerializable("product", product)
        }

        val intent = Intent(requireContext(), ProductAdminDetailActivity::class.java).apply { putExtras(bundle) }
        startActivity(intent)
    }

    private fun getProducts() {
        val collectionProduct = Firebase.firestore.collection("Producto")

        GlobalScope.launch(Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val response = withContext(Dispatchers.IO) {
                    collectionProduct.get().await().toObjects(Product::class.java)
                }

                productAdapter.updateList(response)
            } catch (ex: Exception) {
                print(ex.message)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

}