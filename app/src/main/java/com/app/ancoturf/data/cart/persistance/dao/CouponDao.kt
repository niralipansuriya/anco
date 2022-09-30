package com.app.ancoturf.data.cart.persistance.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.common.local.BaseDao
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface CouponDao : BaseDao<CouponDB> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insetAsync(orderDB: CouponDB):Single<Long>

    @Query(value = "SELECT * FROM " + CouponDB.TABLE_NAME + " WHERE  " + CouponDB.COLUMN_ID + " = :id")
    override fun getItem(id: Long): Single<CouponDB>

    @Query("SELECT * FROM " + CouponDB.TABLE_NAME + " ORDER BY " + CouponDB.COLUMN_ID + " ASC")
    override fun getItems(): Single<List<CouponDB>>

    @Query("DELETE FROM " + CouponDB.TABLE_NAME)
    override fun deleteAll()

    @Query("DELETE FROM " + CouponDB.TABLE_NAME+ " WHERE  " + CouponDB.COLUMN_ID + " = :id")
    override fun deleteItem(id: Long)

}