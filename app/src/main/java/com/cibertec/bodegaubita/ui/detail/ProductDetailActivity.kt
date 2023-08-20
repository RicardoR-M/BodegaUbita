package com.cibertec.bodegaubita.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cibertec.bodegaubita.R
import com.cibertec.bodegaubita.common.Utils.Companion.numToSol
import com.cibertec.bodegaubita.common.Utils.Companion.productToCartItem
import com.cibertec.bodegaubita.databinding.ActivityProductDetailBinding
import com.cibertec.bodegaubita.localdb.LocalDatabase
import com.cibertec.bodegaubita.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        init()
        events()
    }

    override fun onStart() {
        super.onStart()
        val product = intent.extras?.getSerializable("product") as Product
        checkFavorite(product.id)
    }

    private fun init(): Product {
        auth = Firebase.auth
        // extract Product from bundle
        val product = intent.extras?.getSerializable("product") as Product

        with(binding) {
            tvProductName.text = product.name
            tvProductDescription.text = product.description
            tvProductPrice.text = numToSol(product.price)
            tvProductQuantity.text = product.unit

            if (product.image.isNotEmpty()) {
                Picasso.get().load(product.image).into(ivProductImage)
            }
        }
        return product
    }

    private fun events() {
        val product = intent.extras?.getSerializable("product") as Product

        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.btnAddToCart.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    withContext(Dispatchers.IO) {
                        LocalDatabase.getInstance(this@ProductDetailActivity).cartItemDao().upsert(productToCartItem(product))
                    }
                    Toast.makeText(this@ProductDetailActivity, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                } catch (ex: Exception) {
                    Log.e("SAVE_ERROR", "Error saving product to cart", ex)
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }

        }

        binding.btnFavorite.setOnClickListener {
            val userUid = auth.currentUser?.uid

            if (userUid != null) {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        binding.progressBar.visibility = View.VISIBLE

                        val favoriteDocument = withContext(Dispatchers.IO) {
                            Firebase.firestore.collection("Favorite").document(userUid).get().await()
                        }

                        if (favoriteDocument.exists()){
                            var productList = favoriteDocument.get("products") as List<String>

                            if (!productList.contains(product.id)){
                                productList = productList + product.id
                                binding.btnFavorite.setImageResource(R.drawable.icon_fav)
                            } else {
                                productList = productList - product.id
                                binding.btnFavorite.setImageResource(R.drawable.icon_not_fav)
                            }

                            withContext(Dispatchers.IO){
                                Firebase.firestore.collection("Favorite").document(userUid).update("products", productList).await()
                            }
                        } else {
                            val productList = mutableListOf<String>()
                            productList.add(product.id)

                            withContext(Dispatchers.IO){
                                Firebase.firestore.collection("Favorite").document(userUid).set(mapOf("products" to productList)).await()
                            }
                            binding.btnFavorite.setImageResource(R.drawable.icon_fav)
                        }
                    } catch (ex: Exception) {
                        Log.e("SAVE_ERROR", "Error saving product to favorites", ex)
                    } finally {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun checkFavorite(productId: String) {
        val userUid = auth.currentUser?.uid

        if (userUid != null) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    val favoriteDocument = withContext(Dispatchers.IO) {
                        Firebase.firestore.collection("Favorite").document(userUid).get().await()
                    }

                    if (favoriteDocument.exists()){
                        val productList = favoriteDocument.get("products") as List<String>

                        if (productList.contains(productId)){
                            binding.btnFavorite.setImageResource(R.drawable.icon_fav)
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.icon_not_fav)
                        }
                    }
                } catch (ex: Exception) {
                    Log.e("SAVE_ERROR", "Error saving product to favorites", ex)
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}