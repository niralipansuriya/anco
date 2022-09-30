package com.app.ancoturf.data.cart.persistance.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.common.local.BaseDao
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface ProductDao : BaseDao<ProductDB> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insetAsync(orderDB: ProductDB):Single<Long>

    @Query(value = "SELECT * FROM " + ProductDB.TABLE_NAME + " WHERE  " + ProductDB.COLUMN_ID + " = :id")
    override fun getItem(id: Long): Single<ProductDB>

    @Query("SELECT * FROM " + ProductDB.TABLE_NAME + " ORDER BY " + ProductDB.COLUMN_ID + " ASC")
    override fun getItems(): Single<List<ProductDB>>

    @Query("DELETE FROM " + ProductDB.TABLE_NAME)
    override fun deleteAll()

    @Query("DELETE FROM " + ProductDB.TABLE_NAME+ " WHERE  " + ProductDB.COLUMN_ID + " = :id")
    override fun deleteItem(id: Long)

}