package com.cibertec.bodegaubita.localdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.cibertec.bodegaubita.model.OrderItem

@Dao
interface CartItemDao {
    // Upsert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(orderItem: OrderItem)

    @Query("SELECT COUNT(*) FROM table_cart WHERE productId = :fsId")
    fun countItemsWithFsId(fsId: String): Int

    @Transaction
    fun upsert(orderItem: OrderItem) {
        val count = countItemsWithFsId(orderItem.productId)
        if (count > 0) {
            increaseQuantity(orderItem.productId)
        } else {
            insert(orderItem)
        }
    }

    // Decrease and checks that the quantity is greater than 1
    @Query("UPDATE table_cart SET quantity = quantity - 1 WHERE productId = :fsId AND quantity > 1")
    fun decreaseQuantity(fsId: String)

    // Increase the quantity
    @Query("UPDATE table_cart SET quantity = quantity + 1 WHERE productId = :fsId")
    fun increaseQuantity(fsId: String)


    // Update
    @Update
    fun update(orderItem: OrderItem)

    // Delete
    @Delete
    fun delete(orderItem: OrderItem)

    // Get all items of the cart
    @Query("SELECT * FROM table_cart ORDER BY local_id DESC")
    fun getAll(): List<OrderItem>

    // Get total price of the cart
    @Query("SELECT SUM(price * quantity) FROM table_cart")
    fun getTotalPrice(): Double

    // Delete all items of the cart
    @Query("DELETE FROM table_cart")
    fun deleteAll()

}