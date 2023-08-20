package com.cibertec.bodegaubita.ui.admin.product

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.cibertec.bodegaubita.databinding.ActivityProductAdminDetailBinding
import com.cibertec.bodegaubita.model.Category
import com.cibertec.bodegaubita.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductAdminDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductAdminDetailBinding
    private var imageUri = Uri.EMPTY
    private var pickImageFlag: Boolean = false
    private var categoryList = mutableListOf<Category>()
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        binding.ivProductImage.setImageURI(uri)
        imageUri = uri
        pickImageFlag = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductAdminDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        events()

        val product = intent.getSerializableExtra("product") as? Product
        binding.spCategory.threshold = Int.MAX_VALUE

        if (product != null) {
            binding.edtName.setText(product.name)
            binding.edtUnit.setText(product.unit)
            binding.edtDescription.setText(product.description)
            binding.edtPrice.setText(product.price.toString())

            binding.btnAddProduct.setText("Actualizar Producto")

            if (product.image.isNotEmpty()) {
                Picasso.get()
                    .load(product.image)
                    .into(binding.ivProductImage)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val product = intent.getSerializableExtra("product") as? Product
        populateCategoryDropdown {
            if (product != null) {
                val category = categoryList.find { it.id == product.categoryId }
                if (category != null) {
                    binding.spCategory.setText(category.name)
                }
            }
        }
    }

    private fun events() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivProductImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAddProduct.setOnClickListener {
            val productBundle = intent.getSerializableExtra("product") as? Product
            val name = binding.edtName.text.toString()
            val unit = binding.edtUnit.text.toString()
            val description = binding.edtDescription.text.toString()
            val price = binding.edtPrice.text.toString().toDoubleOrNull()
            val category = binding.spCategory.text.toString()

            if (name.isEmpty() || unit.isEmpty() || description.isEmpty() || price == null || category.isEmpty()) {
                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // upload image to firebase storage
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    var imageUrl = ""
                    if (pickImageFlag) {
                        imageUrl = withContext(Dispatchers.IO) {
                            val categoryImageRef = Firebase.storage.reference.child("Product/${name}.jpg")
                            categoryImageRef.putFile(imageUri).await()
                            categoryImageRef.downloadUrl.await().toString()
                        }
                    }

                    if (productBundle != null && !pickImageFlag) {
                        imageUrl = productBundle.image
                    }

                    val product = Product(
                        name = name,
                        unit = unit,
                        description = description,
                        price = price,
                        image = imageUrl,
                        categoryId = categoryList.find { it.name == category }?.id ?: ""
                    )

                    val productRef = Firebase.firestore.collection("Producto")
                    withContext(Dispatchers.IO) {
                        if (productBundle == null) {
                            productRef.add(product).await()
                        } else {
                            productRef.document(productBundle.id).set(product).await()
                        }
                    }

                    Toast.makeText(this@ProductAdminDetailActivity, "Producto registrado/actualizado correctamente", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(this@ProductAdminDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.progressBar.visibility = View.GONE
                    finish()
                }
            }
        }
    }

    private suspend fun fetchCategories(): List<Category> {
        val categoryList = mutableListOf<Category>()
        val categoryDocuments = Firebase.firestore.collection("Category").get().await()
        for (categoryDocument in categoryDocuments) {
            val category = categoryDocument.toObject(Category::class.java)
            categoryList.add(category)
        }
        return categoryList
    }

    private fun populateCategoryDropdown(callback: (() -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            categoryList = withContext(Dispatchers.IO) {
                fetchCategories()
            } as MutableList<Category>
            val adapter = ArrayAdapter(this@ProductAdminDetailActivity, android.R.layout.simple_dropdown_item_1line, categoryList.map { it.name })
            binding.spCategory.setAdapter(adapter)
            callback?.invoke()
        }
    }
}