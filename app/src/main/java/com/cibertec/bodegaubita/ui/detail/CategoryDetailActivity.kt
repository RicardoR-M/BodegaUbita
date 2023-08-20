package com.cibertec.bodegaubita.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.cibertec.bodegaubita.common.Utils.Companion.productToCartItem
import com.cibertec.bodegaubita.databinding.ActivityCategoryDetailBinding
import com.cibertec.bodegaubita.localdb.LocalDatabase
import com.cibertec.bodegaubita.model.Category
import com.cibertec.bodegaubita.model.Product
import com.cibertec.bodegaubita.ui.store.ProductAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding
    private var categoryId: String? = null

    private var productAdapter = ProductAdapter(
        onItemAdd = { product -> onItemAdd(product) },
        onItemDetail = { product -> onItemDetail(product) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //extract the category id from the intent
        val category = intent.getSerializableExtra("category") as Category
        binding.tvTitle.text = category.name
        categoryId = category.id

        init()
        events()
    }

    private fun events() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        categoryId?.let { getProductsByCategory(it) }
    }

    private fun init() {
        binding.rvCategoryProducts.adapter = productAdapter
    }

    private fun onItemAdd(product: Product) {
        GlobalScope.launch (Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                withContext(Dispatchers.IO){
                    LocalDatabase.getInstance(this@CategoryDetailActivity).cartItemDao().upsert(productToCartItem(product))
                }
            } catch (ex: Exception) {
                print(ex.message)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun onItemDetail(product: Product) {
        val bundle = Bundle().apply {
            putSerializable("product", product)
        }

        val intent = Intent(this, ProductDetailActivity::class.java).apply { putExtras(bundle) }
        startActivity(intent)
    }

    private fun getProductsByCategory(categoryId: String) {
        val productsCollection = Firebase.firestore.collection("Producto")
        GlobalScope.launch(Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val response = withContext(Dispatchers.IO) {
                    productsCollection
                        .whereEqualTo("categoryId", categoryId)
                        .get()
                        .await()
                        .toObjects(Product::class.java)
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