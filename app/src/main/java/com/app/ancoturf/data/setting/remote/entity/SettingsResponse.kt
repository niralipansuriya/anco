package com.app.ancoturf.data.setting.remote.entity


import com.google.gson.annotations.SerializedName

data class SettingsResponse(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("success")
    var success: Boolean = false
) {
    data class Data(
        @SerializedName("anco_settings")
        var ancoSettings: AncoSettings = AncoSettings(),
        @SerializedName("contact_screen_setting")
        var contactScreenSetting: ContactScreenSetting = ContactScreenSetting(),
        @SerializedName("delivery_statuses")
        var deliveryStatuses: List<DeliveryStatus> = listOf(),
        @SerializedName("onboarding_screen_setting")
        var onboardingScreenSetting: OnboardingScreenSetting = OnboardingScreenSetting(),
        @SerializedName("order_statuses")
        var orderStatuses: List<OrderStatus> = listOf(),
        @SerializedName("orders")
        var orders: Orders = Orders(),
        @SerializedName("product")
        var product: Product = Product(),
        @SerializedName("product_units")
        var productUnits: List<ProductUnit> = listOf(),
        @SerializedName("quote")
        var quote: Quote = Quote(),
        @SerializedName("roles")
        var roles: List<Role> = listOf(),
        @SerializedName("signin_screen_setting")
        var signinScreenSetting: SigninScreenSetting = SigninScreenSetting(),
        @SerializedName("signup_screen_setting")
        var signupScreenSetting: SignupScreenSetting = SignupScreenSetting(),
        @SerializedName("tags")
        var tags: List<Tag> = listOf()
    ) {
        data class Product(
            @SerializedName("max_price")
            var maxPrice: String = ""
        )

        data class SignupScreenSetting(
            @SerializedName("signup_background_image")
            var signupBackgroundImage: String = "",
            @SerializedName("signup_background_image_ipad")
            var signupBackgroundImageIpad: String = "",
            @SerializedName("signup_background_image_iphone")
            var signupBackgroundImageIphone: String = "",
            @SerializedName("signup_background_image_tablet")
            var signupBackgroundImageTablet: String = "",
            @SerializedName("signup_logo")
            var signupLogo: String = "",
            @SerializedName("signup_logo_ipad")
            var signupLogoIpad: String = ""
        )

        data class DeliveryStatus(
            @SerializedName("display_name")
            var displayName: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("slug")
            var slug: String = "",
            @SerializedName("status")
            var status: String = "",
            var checked: Boolean = false
        )

        data class SigninScreenSetting(
            @SerializedName("signin_background_image")
            var signinBackgroundImage: String = "",
            @SerializedName("signin_background_image_ipad")
            var signinBackgroundImageIpad: String = "",
            @SerializedName("signin_background_image_iphone")
            var signinBackgroundImageIphone: String = "",
            @SerializedName("signin_background_image_tablet")
            var signinBackgroundImageTablet: String = "",
            @SerializedName("signin_logo")
            var signinLogo: String = "",
            @SerializedName("signin_logo_ipad")
            var signinLogoIpad: String = ""
        )

        data class AncoSettings(
            @SerializedName("admin_email")
            var adminEmail: String = "",
            @SerializedName("allow_images_non_anco_products_portfolio")
            var allowImagesNonAncoProductsPortfolio: Int = 0,
            @SerializedName("australia_post_api_key")
            var australiaPostApiKey: String = "",
            @SerializedName("australia_post_default_package_height")
            var australiaPostDefaultPackageHeight: Int = 0,
            @SerializedName("australia_post_default_package_length")
            var australiaPostDefaultPackageLength: Int = 0,
            @SerializedName("australia_post_default_package_weight")
            var australiaPostDefaultPackageWeight: Double = 0.0,
            @SerializedName("australia_post_default_package_width")
            var australiaPostDefaultPackageWidth: Int = 0,
            @SerializedName("australia_post_shop_origin_postal_code")
            var australiaPostShopOriginPostalCode: Int = 0,
            @SerializedName("credits_allowed_in_products")
            var creditsAllowedInProducts: String = "",
            @SerializedName("credits_allowed_to_roles")
            var creditsAllowedToRoles: String = "",
            @SerializedName("credits_earned_after_milestone_achieved")
            var creditsEarnedAfterMilestoneAchieved: Int = 0,
            @SerializedName("credits_notification_before_days")
            var creditsNotificationBeforeDays: Int = 0,
            @SerializedName("credits_quantity_milestone")
            var creditsQuantityMilestone: Int = 0,
            @SerializedName("delivery_max_days")
            var deliveryMaxDays: Int = 0,
            @SerializedName("delivery_message")
            var deliveryMessage: String = "",
            @SerializedName("delivery_min_days")
            var deliveryMinDays: Int = 0,
            @SerializedName("max_images_non_anco_products_quote")
            var maxImagesNonAncoProductsQuote: Int = 0,
            @SerializedName("max_number_portfolios")
            var maxNumberPortfolios: Int = 0,
            @SerializedName("max_portfolio_images")
            var maxPortfolioImages: Int = 0,
            @SerializedName("recommended_products")
            var recommendedProducts: String = "",
            @SerializedName("shipping_small_quantity")
            var shippingSmallQuantity: Int = 0,
            @SerializedName("website_url")
            var websiteUrl: String = "",
            @SerializedName("quote_to_order_message")
            var quoteToOrderMessage: String = "",
            @SerializedName("drop_turf_on_property_title")
            var dropTurfOnPropertyTitle: String = "",
            @SerializedName("drop_turf_on_property_description")
            var dropTurfOnPropertyDescription: String = "",
            @SerializedName("accept_decline_checkbox_title")
            var acceptDeclineCheckboxTitle: String = "",
            @SerializedName("click_collect_checkbox_title")
            var clickCollectCheckboxTitle: String = "",
            @SerializedName("click_collect_checkbox_description")
            var clickCollectCheckboxDescription: String = "",
            @SerializedName("torquy_click_collect_checkbox_description")
            var torquy_click_collect_checkbox_description: String = "",
            @SerializedName("admin_fee")
            var adminFee: String = ""
        )

        data class ProductUnit(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("name")
            var name: String = ""
        )

        data class Quote(
            @SerializedName("max_price")
            var maxPrice: Long = 0
        )

        data class Role(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("is_superadmin")
            var isSuperadmin: String = "",
            @SerializedName("name")
            var name: String = ""
        )

        data class OrderStatus(
            @SerializedName("display_name")
            var displayName: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("slug")
            var slug: String = "",
            @SerializedName("status")
            var status: String = "",
            var checked: Boolean = false
        )

        data class Tag(
            @SerializedName("display_name")
            var displayName: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("slug")
            var slug: String = "",
            var selected: Boolean = false
        )

        data class ContactScreenSetting(
            @SerializedName("contact_address_line_1")
            var contactAddressLine1: String = "",
            @SerializedName("contact_address_line_2")
            var contactAddressLine2: String = "",
            @SerializedName("contact_address_line_3")
            var contactAddressLine3: String = "",
            @SerializedName("contact_background_image")
            var contactBackgroundImage: String = "",
            @SerializedName("contact_background_image_ipad")
            var contactBackgroundImageIpad: String = "",
            @SerializedName("contact_background_image_iphone")
            var contactBackgroundImageIphone: String = "",
            @SerializedName("contact_background_image_tablet")
            var contactBackgroundImageTablet: String = "",
            @SerializedName("contact_email")
            var contactEmail: String = "",
            @SerializedName("contact_label")
            var contactLabel: String = "",
            @SerializedName("contact_logo")
            var contactLogo: String = "",
            @SerializedName("contact_logo_ipad")
            var contactLogoIpad: String = "",
            @SerializedName("contact_operating_hours_label")
            var contactOperatingHoursLabel: String = "",
            @SerializedName("contact_operating_hours_line_1")
            var contactOperatingHoursLine1: String = "",
            @SerializedName("contact_operating_hours_line_2")
            var contactOperatingHoursLine2: String = "",
            @SerializedName("contact_operating_hours_line_3")
            var contactOperatingHoursLine3: String = "",
            @SerializedName("contact_operating_hours_line_4")
            var contactOperatingHoursLine4: String = "",
            @SerializedName("contact_phone")
            var contactPhone: String = ""
        )

        data class Orders(
            @SerializedName("max_price")
            var maxPrice: Long = 0
        )

        data class OnboardingScreenSetting(
            @SerializedName("onboarding_background_image")
            var onboardingBackgroundImage: String = "",
            @SerializedName("onboarding_background_image_ipad")
            var onboardingBackgroundImageIpad: String = "",
            @SerializedName("onboarding_background_image_iphone")
            var onboardingBackgroundImageIphone: String = "",
            @SerializedName("onboarding_background_image_tablet")
            var onboardingBackgroundImageTablet: String = "",
            @SerializedName("onboarding_logo")
            var onboardingLogo: String = "",
            @SerializedName("onboarding_logo_ipad")
            var onboardingLogoIpad: String = ""
        )
    }
}