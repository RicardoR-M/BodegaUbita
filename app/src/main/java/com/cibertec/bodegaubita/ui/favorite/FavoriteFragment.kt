package com.cibertec.bodegaubita.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cibertec.bodegaubita.common.Utils.Companion.productToCartItem
import com.cibertec.bodegaubita.databinding.FragmentFavoriteBinding
import com.cibertec.bodegaubita.localdb.LocalDatabase
import com.cibertec.bodegaubita.ui.detail.ProductDetailActivity
import com.cibertec.bodegaubita.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var auth: FirebaseAuth
    private var products = listOf<Product>()

    private var favoriteAdapter = FavoriteAdapter(
        onItemDetail = { product -> onItemDetail(product) })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        init()
        events()

        return binding.root
    }

    private fun events() {
        binding.btnAddAll.setOnClickListener {
            // save all products to cart in the local database using room

            GlobalScope.launch (Dispatchers.Main) {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    withContext(Dispatchers.IO){
                        products.forEach { product ->
                            LocalDatabase.getInstance(requireContext()).cartItemDao().upsert(productToCartItem(product)) }
                    }
                    Toast.makeText(context, "Se agregaron todos los productos al carrito", Toast.LENGTH_SHORT).show()
                } catch (ex: Exception) {
                    Log.e("FavoriteFragment", "Error al agregar todos los productos al carrito", ex)
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun init() {
        binding.rvFavorite.adapter = favoriteAdapter
        auth = Firebase.auth

    }

    override fun onStart() {
        super.onStart()
        getFavorites()
    }

    private fun getFavorites() {
        val userUid = auth.currentUser?.uid

        if (userUid != null) {

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    val favoriteDocument = withContext(Dispatchers.IO) {
                        Firebase.firestore.collection("Favorite").document(userUid).get().await()
                    }

                    if (favoriteDocument.exists()) {
                        val productList = favoriteDocument.get("products") as List<String>

//                        var products = listOf<Product>()

                        if (productList.isNotEmpty())
                        {
                            products = withContext(Dispatchers.IO) {
                                Firebase.firestore.collection("Producto").whereIn(FieldPath.documentId(), productList).get().await().toObjects(Product::class.java)
                            }
                        }

                        if (products.isEmpty()) {
                            binding.btnAddAll.isEnabled = false
                            binding.btnAddAll.alpha = 0.5f
                        } else {
                            binding.btnAddAll.isEnabled = true
                            binding.btnAddAll.alpha = 1f
                        }

                        favoriteAdapter.updateList(products)
                    } else {
                        Log.e("FavoriteFragment", "No existe el documento")
                    }
                } catch (ex: Exception) {
                    Log.e("FavoriteFragment", "Error al obtener los favoritos", ex)
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun onItemDetail(product: Product) {
        val bundle = Bundle().apply {
            putSerializable("product", product)
        }

        val intent = Intent(context, ProductDetailActivity::class.java).apply { putExtras(bundle) }
        startActivity(intent)
    }
}