package com.app.ancoturf.data.common.local

import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Single

interface BaseDao<T> {

    @Insert
    @Ignore
    fun insert(region: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Ignore
    fun insert(items: List<T>): List<Long>

    @Update
    fun update(items: T)

    @Update
    fun update(items: List<T>)

    @Delete
    fun delete(item: T)

    @Delete
    fun delete(items: List<T>)

    fun deleteAll()

    fun getItem(id: Long): Single<T>

    fun deleteItem(id: Long)

    fun getItems(): Single<List<T>>
}