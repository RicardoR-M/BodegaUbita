package com.cibertec.bodegaubita.common

import android.graphics.Bitmap
import android.util.Base64
import com.cibertec.bodegaubita.model.OrderItem
import com.cibertec.bodegaubita.model.Product
import java.io.ByteArrayOutputStream


class Utils {
    companion object {
        fun numToSol(num: Double): String {
            return String.format("S/. %.2f", num)
        }

        fun cartItemsToProducts(orderItems: List<OrderItem>): List<Product> {
            return orderItems.map { cartItem ->
                Product(
                    id = cartItem.productId,
                    name = cartItem.name,
                    unit = cartItem.unit,
                    description = cartItem.description,
                    image = cartItem.image,
                    price = cartItem.price,
                    categoryId = cartItem.categoryId
                )
            }
        }

        fun productToCartItem(product: Product): OrderItem {
            return OrderItem(
                local_id = 0,
                productId = product.id,
                name = product.name,
                unit = product.unit,
                description = product.description,
                image = product.image,
                price = product.price,
                categoryId = product.categoryId,
                quantity = 1
            )
        }

        fun cartItemsToTotalPrice(orderItems: List<OrderItem>): Double {
            return orderItems.map { cartItem ->
                cartItem.price * cartItem.quantity
            }.sum()
        }

        fun converterBase64(imageBitmap: Bitmap): String {
            val bytesArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytesArrayOutputStream)
            val bytes = bytesArrayOutputStream.toByteArray()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }
}

