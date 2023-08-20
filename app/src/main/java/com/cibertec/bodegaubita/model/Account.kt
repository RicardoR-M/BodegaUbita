package com.cibertec.bodegaubita.model

import com.google.firebase.firestore.DocumentId

data class Account(
    @DocumentId
    val id: String = "",
    val uid: String = "",
    val address: String = ""
)

