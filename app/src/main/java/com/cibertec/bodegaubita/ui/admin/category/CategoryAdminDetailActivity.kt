package com.cibertec.bodegaubita.ui.admin.category

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.cibertec.bodegaubita.databinding.ActivityCategoryAdminDetailBinding
import com.cibertec.bodegaubita.model.Category
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CategoryAdminDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryAdminDetailBinding
    private var imageUri = Uri.EMPTY
    private var pickImageFlag: Boolean = false
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        binding.ivCategoryImage.setImageURI(uri)
        imageUri = uri
        pickImageFlag = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAdminDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        events()

        val category = intent.getSerializableExtra("category") as? Category

        if (category != null) {
            binding.edtName.setText(category.name)
            binding.edtColor.setText(category.background)
            binding.btnAddCategory.setText("Actualizar Categoria")

            if (category.image.isNotEmpty()) {
                Picasso.get()
                    .load(category.image)
                    .into(binding.ivCategoryImage)
            }
        }
    }

    private fun events() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivCategoryImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAddCategory.setOnClickListener {
            val categoryBundle = intent.getSerializableExtra("category") as? Category
            val name = binding.edtName.text.toString()
            val color = binding.edtColor.text.toString()

            // upload image to firebase storage
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    // generate the name plus a random number
                    var imageUrl = ""
                    if (pickImageFlag) {
                        imageUrl = withContext(Dispatchers.IO) {
                            val categoryImageRef = Firebase.storage.reference.child("Category/${name}_${color.replace("#", "")}.jpg")
                            categoryImageRef.putFile(imageUri).await()
                            categoryImageRef.downloadUrl.await().toString()
                        }
                    }

                    if (categoryBundle != null && !pickImageFlag) {
                        imageUrl = categoryBundle.image
                    }

                    val category = Category(name = name, background = color, image = imageUrl)

                    // save category to firebase database
                    val categoryRef = Firebase.firestore.collection("Category")
                    withContext(Dispatchers.IO) {
                        if (categoryBundle == null) {
                            categoryRef.add(category).await()
                        } else {
                            categoryRef.document(categoryBundle.id).set(category).await()
                        }
                    }

                    Toast.makeText(this@CategoryAdminDetailActivity, "Categoria guardada correctamente", Toast.LENGTH_SHORT).show()
                    finish()

                } catch (e: Exception) {
                    Toast.makeText(this@CategoryAdminDetailActivity, "Error al crear/modificar categoria", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}