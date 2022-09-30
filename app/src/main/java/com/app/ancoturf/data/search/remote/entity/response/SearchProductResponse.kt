package com.app.ancoturf.data.search.remote.entity.response


import com.google.gson.annotations.SerializedName

data class SearchProductResponse(
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("last_searched_products")
    var lastSearchedProducts : ArrayList<SearchedProducts>,
    @SerializedName("recommanded_products")
    var recommandedProducts : ArrayList<SearchedProducts>
) {
    data class SearchedProducts(
        @SerializedName("id")
        var id: Int = 0 ,
        @SerializedName("product_name")
        var productName: String = "",
        @SerializedName("product_category")
        var productCategory: String = "",
        @SerializedName("feature_image_url")
        var featureImageUrl: String = "",
        @SerializedName("cms_privilege_id")
        var cmsPrivilegeId: Int = 0,
        @SerializedName("product_redeemable_against_credit")
        var productRedeemableAgainstCredit: Boolean = false

    )
}