package com.cibertec.bodegaubita.model

import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Category(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val background: String = ""
): Serializable
