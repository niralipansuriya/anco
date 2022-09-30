package com.app.ancoturf.data.cart.persistance.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = ProductDB.TABLE_NAME)
data class ProductDB(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_ID)
    @SerializedName("id")
    var product_id: Int? = 0,
    @SerializedName("feature_image_url")
    var feature_img_url: String? = "",
    @SerializedName("qty")
    var qty: Int = 0,
    @SerializedName("price")
    var price: Float? = 0f,
    @SerializedName("product_category_id")
    val product_category_id: Int? = 0,
    @SerializedName("product_name")
    var product_name: String? = "",
    @SerializedName("product_unit")
    val product_unit: String? = "",
    @SerializedName("product_unit_id")
    val product_unit_id: Int? = 0,
    @SerializedName("base_total_price")
    var base_total_price: Float? = 0f,
    @SerializedName("is_turf")
    var is_turf: Int? = 0,
    @SerializedName("total_price")
    var total_price: Float? = 0f,
    @SerializedName("product_redeemable_against_credit")
    var product_redeemable_against_credit: Boolean = false


) {
    @Ignore
    var free_product: Int = 0

    @Ignore
    var is_free_products: Int = 0

    @Ignore
    var free_product_id: Int = 0

    @Ignore
    var free_product_qty: Int = 0

    @Ignore
    var free_products: FreeProducts = FreeProducts()



    companion object {
        const val TABLE_NAME = "product_table"
        const val COLUMN_ID = "id"
    }

    data class FreeProducts(
        var qty: Int = 0,
        var product_id: Int = 0,
        var free_product_id: Int = 0,
        var id: Int = 0,
        var minimum_qty: Int = 0,
        var feature_image_url: String = "",
        var price: Float = 0f,
        @SerializedName("product_name")
        var productName: String = "",
        var free_product_description: String = "",
        @SerializedName("product_category_id")
        var productCategoryId: Int = 0
    )
}

//{
//    "id": 5,
//    "product_category_id": "1",
//    "price": "8.85",
//    "feature_image_url": "http://anco.anasource.com:90/uploads/1/2019-06/c910581ddbd7d8e365c929e48752a8ac.jpg",
//    "product_unit": "Square meter",
//    "product_unit_id": "2",
//    "product_name": "Nullarbor Couch",
//    "is_turf": "1",
//    "weight": "0.00",
//    "dimension_length": "0.00",
//    "dimension_width": "0.00",
//    "dimension_height": "0.00",
//    "total_price": 885,
//    "quantity": 100,
//    "qty": 100,
//    "base_total_price": 885,
//    "product_redeemable_against_credit": true
//}

