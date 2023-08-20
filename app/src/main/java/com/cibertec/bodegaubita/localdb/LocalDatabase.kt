package com.cibertec.bodegaubita.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cibertec.bodegaubita.model.OrderItem

@Database(entities = [OrderItem::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao

    companion object {
        var localDatabase: LocalDatabase? = null

        fun getInstance(context: Context): LocalDatabase {
            if (localDatabase == null) {
                localDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "db_bodega_ubita"
                ).fallbackToDestructiveMigration()
                    .build()
            }
            return localDatabase!!
        }
    }

}