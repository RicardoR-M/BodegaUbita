package com.cibertec.bodegaubita.ui.cart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cibertec.bodegaubita.common.Utils.Companion.numToSol
import com.cibertec.bodegaubita.databinding.FragmentCartBinding
import com.cibertec.bodegaubita.localdb.LocalDatabase
import com.cibertec.bodegaubita.model.Order
import com.cibertec.bodegaubita.model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth

    private var cartAdapter = CartAdapter(
        onItemDelete = { cartItem -> onItemDelete(cartItem) },
        onItemIncrease = { productId -> onItemIncrease(productId) },
        onItemDecrease = { productId -> onItemDecrease(productId) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCartBinding.inflate(inflater, container, false)

        init()
        events()

        return binding.root
    }

    private fun events() {
        val collectionOrder = Firebase.firestore.collection("Order")

        binding.btnCheckOut.setOnClickListener {
            // empties the cart
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    val response = withContext(Dispatchers.IO) {
                        val order = auth.currentUser?.let { it1 ->
                            Order(
                                products = LocalDatabase.getInstance(requireContext()).cartItemDao().getAll(),
                                userUid = it1.uid,
                            )
                        }
                        collectionOrder.add(order!!).await()
                        LocalDatabase.getInstance(requireContext()).cartItemDao().deleteAll()
                    }

                    // launch the SuccessActivity
                    val intent = Intent(context, SuccessActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al realizar la compra", Toast.LENGTH_SHORT).show()
                    Log.e("CartFragment", "Error al realizar la compra", e)
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getCart()
    }

    private fun init() {
        binding.rvCart.adapter = cartAdapter
        auth = Firebase.auth
    }

    private fun onItemDecrease(productId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    LocalDatabase.getInstance(requireContext()).cartItemDao().decreaseQuantity(productId)
                }
                getCart()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al disminuir la cantidad de productos", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun onItemIncrease(productId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    LocalDatabase.getInstance(requireContext()).cartItemDao().increaseQuantity(productId)
                }
                getCart()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al incrementar la cantidad de productos", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun onItemDelete(orderItem: OrderItem) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                withContext(Dispatchers.IO) {
                    LocalDatabase.getInstance(requireContext()).cartItemDao().delete(orderItem)
                }
                getCart()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al eliminar producto", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getCart() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val cartList = withContext(Dispatchers.IO) {
                    LocalDatabase.getInstance(requireContext()).cartItemDao().getAll()
                }

                cartAdapter.updateList(cartList)
                getTotalPrice()

                //if cartlist is empty disables the checkout button
                if (cartList.isEmpty()) {
                    binding.btnCheckOut.isEnabled = false
                    binding.btnCheckOut.alpha = 0.5f
                    binding.tvCartTotal.visibility = View.GONE
                } else {
                    binding.btnCheckOut.isEnabled = true
                    binding.btnCheckOut.alpha = 1f
                    binding.tvCartTotal.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Log.e("CartFragment", "Error al obtener carrito", e)
                Toast.makeText(context, "Error al obtener carrito", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getTotalPrice() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val totalPrice = withContext(Dispatchers.IO) {
                    LocalDatabase.getInstance(requireContext()).cartItemDao().getTotalPrice()
                }
                binding.tvCartTotal.text = numToSol(totalPrice)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al obtener carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }

}