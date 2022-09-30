package com.app.ancoturf.data.cart.persistance.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CouponDB.TABLE_NAME)
data class CouponDB(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    val id: Int? = 0,
    val code: String? = null,
    val discount: String? = null,
    val discount_type: String? = null,
    val expiry_date: String? = null,
    val item_ids: String? = null,
    val item_type: String? = null,
    val name: String? = null,
    val start_date: String? = null,
    val status: String? = null,
    val thereshold: String? = null,
    val user_role_ids: String? = null,
    val user_type: String? = null
) {

    companion object {
        const val TABLE_NAME = "coupon_table"
        const val COLUMN_ID = "id"
    }
}

