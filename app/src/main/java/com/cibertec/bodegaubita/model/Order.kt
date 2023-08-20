package com.cibertec.bodegaubita.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Order(
    @DocumentId
    val id: String = "",
    val delivered: Boolean = false,
    val products: List<OrderItem> = emptyList(),
    val userUid: String = "",
    @Exclude
    var dateMillis: Long = -1L
): Parcelable {
    fun getDate(): Timestamp {
        if (dateMillis == -1L){
            return Timestamp.now()
        }
        return Timestamp(Date(dateMillis))
    }

    fun setDate(timestamp: Timestamp) {
        dateMillis = timestamp.toDate().time
    }
}
