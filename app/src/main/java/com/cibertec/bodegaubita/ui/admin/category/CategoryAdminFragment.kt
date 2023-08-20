package com.cibertec.bodegaubita.ui.admin.category

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.bodegaubita.MainActivity
import com.cibertec.bodegaubita.databinding.FragmentCategoryAdminBinding
import com.cibertec.bodegaubita.model.Category
import com.cibertec.bodegaubita.ui.explore.CategoryAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CategoryAdminFragment : Fragment() {

    private lateinit var binding: FragmentCategoryAdminBinding

    //adapter
    private var categoryAdapter= CategoryAdapter(
        onItemSelected = { category -> onItemSelected(category) }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCategoryAdminBinding.inflate(inflater, container, false)

        init()
        events()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getCategories()
    }

    private fun init() {
        binding.rvCategories.adapter = categoryAdapter
    }

    private fun getCategories() {
        val collectionCategory = Firebase.firestore.collection("Category")

        GlobalScope.launch (Dispatchers.Main){
            try {
                binding.progressBar.visibility = View.VISIBLE
                val response = withContext(Dispatchers.IO){
                    collectionCategory.get().await().toObjects(Category::class.java)
                }

                categoryAdapter.updateList(response)
            } catch (ex: Exception) {
                print(ex.message)
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun events() {
        binding.ivLogo.setOnClickListener {
            // launch an activity
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddCategory.setOnClickListener {
            val intent = Intent(requireContext(), CategoryAdminDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onItemSelected(category: Category) {
        val bundle = Bundle().apply {
            putSerializable("category", category)
        }

        val intent = Intent(requireContext(), CategoryAdminDetailActivity::class.java).apply { putExtras(bundle) }
        startActivity(intent)
    }
}