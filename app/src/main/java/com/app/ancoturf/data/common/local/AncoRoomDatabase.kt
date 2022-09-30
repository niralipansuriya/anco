package com.app.ancoturf.data.common.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.data.cart.persistance.dao.CouponDao
import com.app.ancoturf.data.cart.persistance.dao.ProductDao
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB

@Database(
        entities = [ProductDB::class , CouponDB::class],
        version = BuildConfig.DATABASE_VERSION
)
abstract class AncoRoomDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun couponDao(): CouponDao
}
