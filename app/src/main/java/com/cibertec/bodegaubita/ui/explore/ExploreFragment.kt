package com.cibertec.bodegaubita.ui.explore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cibertec.bodegaubita.databinding.FragmentExploreBinding
import com.cibertec.bodegaubita.ui.detail.CategoryDetailActivity
import com.cibertec.bodegaubita.model.Category
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExploreFragment : Fragment() {

    private lateinit var binding: FragmentExploreBinding

    private var categoryAdapter = CategoryAdapter(
        onItemSelected = { category -> onItemSelected(category) })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getCategories()
    }

    private fun init() {
        binding.rvCategories.adapter = categoryAdapter
    }

    private fun onItemSelected(category: Category) {
        val bundle = Bundle().apply {
            putSerializable("category", category)
        }

        val intent = Intent(context, CategoryDetailActivity::class.java).apply { putExtras(bundle) }
        startActivity(intent)
    }

    private fun getCategories(){
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
}