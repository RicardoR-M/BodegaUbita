package com.cibertec.bodegaubita.model

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Product(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val unit: String = "",
    val description: String = "",
    val image: String = "",
    val price: Double = 0.0,
    val categoryId: String = ""
) : Serializable