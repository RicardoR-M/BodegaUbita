package com.cibertec.bodegaubita.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Entity(tableName = "table_cart")
@Parcelize
data class OrderItem(
    @get:Exclude
    @PrimaryKey(autoGenerate = true)
    var local_id: Int = 0,
    var productId: String = "",
    var name: String = "",
    var unit: String = "",
    var description: String = "",
    var image: String = "",
    var price: Double = 0.0,
    var categoryId: String = "",
    var quantity: Int = 0,
): Parcelable
