package com.cibertec.bodegaubita.ui.store

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cibertec.bodegaubita.common.Utils.Companion.productToCartItem
import com.cibertec.bodegaubita.databinding.FragmentStoreBinding
import com.cibertec.bodegaubita.localdb.LocalDatabase
import com.cibertec.bodegaubita.ui.detail.ProductDetailActivity
import com.cibertec.bodegaubita.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StoreFragment : Fragment() {

    private lateinit var binding: FragmentStoreBinding

    private var productAdapter = ProductAdapter(
        onItemAdd = { product -> onItemAdd(product) },
        onItemDetail = { product -> onItemDetail(product) })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getProducts()
    }

    private fun init() {
        binding.rvExclusiveOffers.adapter = productAdapter
    }

    private fun onItemAdd(product: Product) {
        GlobalScope.launch (Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                withContext(Dispatchers.IO){
                    LocalDatabase.getInstance(requireContext()).cartItemDao().upsert(productToCartItem(product))
                }
            } catch (ex: Exception) {
                print(ex.message)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }

        Toast.makeText(context, "Producto agregado - " + product.name, Toast.LENGTH_SHORT).show()
    }

    private fun onItemDetail(product: Product) {
        val bundle = Bundle().apply {
            putSerializable("product", product)
        }

        val intent = Intent(context, ProductDetailActivity::class.java).apply { putExtras(bundle) }
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