package com.app.ancoturf.data.common.local

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {

    private val sharedPreferences: SharedPreferences

    var sessionId: String
        set(value) = put(PREF_SESSION_ID_NAVMAN, value)
        get() = get(PREF_SESSION_ID_NAVMAN, String::class.java)

    var accessToken: String
        set(value) = put(PREF_SESSION_ACCESS_TOKEN, value)
        get() = get(PREF_SESSION_ACCESS_TOKEN, String::class.java)

    var fcmToken: String
        set(value) = put(FCM_TOKEN, value)
        get() = get(FCM_TOKEN, String::class.java)

    var userId: Int
        set(value) = put(PREF_USER_ID, value)
        get() = get(PREF_USER_ID, Int::class.java)

    var userName: String
        set(value) = put(PREF_USER_NAME, value)
        get() = get(PREF_USER_NAME, String::class.java)

    var userEmail: String
        set(value) = put(PREF_USER_EMAIL, value)
        get() = get(PREF_USER_EMAIL, String::class.java)

    var isWelcomeVisited: Boolean
        set(value) = put(PREF_IS_WELCOME_VISITED, value)
        get() = get(PREF_IS_WELCOME_VISITED, Boolean::class.java)

    var isHowCanWeHelp: Boolean
        set(value) = put(PREF_HOW_CAN_WE_HELP, value)
        get() = get(PREF_HOW_CAN_WE_HELP, Boolean::class.java)

    var isHelpChooseTurf: Boolean
        set(value) = put(PREF_HELP_CHOOSE_TURF, value)
        get() = get(PREF_HELP_CHOOSE_TURF, Boolean::class.java)

    var isHomeOpen: Boolean
        set(value) = put(PREF_OPEN_HOME, value)
        get() = get(PREF_OPEN_HOME, Boolean::class.java)

    var isLogged: Boolean
        set(value) = put(PREF_IS_LOGIN, value)
        get() = get(PREF_IS_LOGIN, Boolean::class.java)

    var isFirstClickTurf: Boolean
        set(value) = put(PREF_IS_FIRST_CLICK_TURF, value)
        get() = get(PREF_IS_FIRST_CLICK_TURF, Boolean::class.java)

    var userType: Int
        set(value) = put(PREF_USER_TYPE, value)
        get() = get(PREF_USER_TYPE, Int::class.java)

    var signinInfo: String
        set(value) = put(Companion.PREF_SIGNIN_INFO, value)
        get() = get(PREF_SIGNIN_INFO, String::class.java)

    var adminFeeNonTurf: String
        set(value) = put(Companion.PREF_ADMIN_FEE, value)
        get() = get(PREF_ADMIN_FEE, String::class.java)

    var postalCode: Int  //nnn done 27/11/2020
        set(value) = put(Companion.PREF_POSTAL_CODE, value)
        get() = get(PREF_POSTAL_CODE, Int::class.java)

   /* var phoneNumber: Int //nnn done 27/11/2020
        set(value) = put(Companion.PREF_PHONE_NUMBER, value)
        get() = get(PREF_PHONE_NUMBER, Int::class.java)*/

    var phoneNumber: String //nnn done 27/11/2020
        set(value) = put(Companion.PREF_PHONE_NUMBER, value)
        get() = get(PREF_PHONE_NUMBER, String()::class.java)

    var signupInfo: String
        set(value) = put(PREF_SIGNUP_INFO, value)
        get() = get(PREF_SIGNUP_INFO, String::class.java)

    var contactInfo: String
        set(value) = put(PREF_CONTACT_INFO, value)
        get() = get(PREF_CONTACT_INFO, String::class.java)

    var onboardingInfo: String
        set(value) = put(PREF_ONBOARDING_INFO, value)
        get() = get(PREF_ONBOARDING_INFO, String::class.java)

    var productUnitsInfo: String
        set(value) = put(PREF_PRODUCT_UNITS, value)
        get() = get(PREF_PRODUCT_UNITS, String::class.java)

    var tags: String
        set(value) = put(PREF_TAGS, value)
        get() = get(PREF_TAGS, String::class.java)

    var product_max_price: String
        set(value) = put(PREF_PRODUCT_MAX_PRICE, value)
        get() = get(PREF_PRODUCT_MAX_PRICE, String::class.java)

    var quote_max_price: String
        set(value) = put(PREF_QUOTE_MAX_PRICE, value)
        get() = get(PREF_QUOTE_MAX_PRICE, String::class.java)

    var order_max_price: String
        set(value) = put(PREF_ORDER_MAX_PRICE, value)
        get() = get(PREF_ORDER_MAX_PRICE, String::class.java)

    var max_portfolio_images: Int
        set(value) = put(PREF_MAX_PORTFOLIO_IMAGES, value)
        get() = get(PREF_MAX_PORTFOLIO_IMAGES, Int::class.java)

    var max_number_portfolios: Int
        set(value) = put(PREF_MAX_NUMBER_PORTFOLIOS, value)
        get() = get(PREF_MAX_NUMBER_PORTFOLIOS, Int::class.java)

    var max_images_non_anco_products_quote: Int
        set(value) = put(PREF_MAX_IMAGES_NON_ANCO_PRODUCTS_QUOTE, value)
        get() = get(PREF_MAX_IMAGES_NON_ANCO_PRODUCTS_QUOTE, Int::class.java)

    var userInfo: String
        set(value) = put(PREF_USER_INFO, value)
        get() = get(PREF_USER_INFO, String::class.java)

    var deliveryMinDays: Int
        set(value) = put(PREF_DELIVERY_MIN_DAYS, value)
        get() = get(PREF_DELIVERY_MIN_DAYS, Int::class.java)

    var deliveryMaxDays: Int
        set(value) = put(PREF_DELIVERY_MAX_DAYS, value)
        get() = get(PREF_DELIVERY_MAX_DAYS, Int::class.java)

    var availableCredit: Int
        set(value) = put(PREF_AVAILABLE_CREDIT, value)
        get() = get(PREF_AVAILABLE_CREDIT, Int::class.java)

    var totalProductsInCart: Int
        set(value) = put(PREF_TOTAL_PRODUCTS_IN_CART, value)
        get() = get(PREF_TOTAL_PRODUCTS_IN_CART, Int::class.java)

    var quoteID: String
        set(value) = put(PREF_QUOTE_ID, value)
        get() = get(PREF_QUOTE_ID, String::class.java)

    var deliveryMessage: String
        set(value) = put(PREF_DELIVERY_MESSAGE, value)
        get() = get(PREF_DELIVERY_MESSAGE, String::class.java)

    var quoteToOrderMessage: String
        set(value) = put(PREF_QUOTE_TO_ORDER_MESSAGE, value)
        get() = get(PREF_QUOTE_TO_ORDER_MESSAGE, String::class.java)

    var dropTurfOnPropertyTitle: String
        set(value) = put(PREF_DROP_TURF_ON_PROPERTY_TITLE, value)
        get() = get(PREF_DROP_TURF_ON_PROPERTY_TITLE, String::class.java)

    var dropTurfOnPropertyDescription: String
        set(value) = put(PREF_DROP_TURF_ON_PROPERTY_DESCRIPTION, value)
        get() = get(PREF_DROP_TURF_ON_PROPERTY_DESCRIPTION, String::class.java)

    var acceptDeclineCheckboxTitle: String
        set(value) = put(PREF_ACCEPT_DECLINE_CHECKBOX_TITLE, value)
        get() = get(PREF_ACCEPT_DECLINE_CHECKBOX_TITLE, String::class.java)

    var clickAndCollectCheckboxDescription: String
        set(value) = put(PREF_CLICK_COLLECT_CHECKBOX_DESCRIPTION, value)
        get() = get(PREF_CLICK_COLLECT_CHECKBOX_DESCRIPTION, String::class.java)

    var clickAndCollectCheckboxTorquayDescription: String
        set(value) = put(PREF_CLICK_COLLECT_CHECKBOX_DESCRIPTION_TORQUAY, value)
        get() = get(PREF_CLICK_COLLECT_CHECKBOX_DESCRIPTION_TORQUAY, String::class.java)

    var clickAndCollectCheckboxTitle: String
        set(value) = put(PREF_CLICK_COLLECT_CHECKBOX_TITLE, value)
        get() = get(PREF_CLICK_COLLECT_CHECKBOX_TITLE, String::class.java)

    var checkUncheckGuideScreenDontShow: Boolean
        set(value) = put(PREF_ACCEPT_GUIDE_SCREEN_DONT_SHOW_CHECKBOX, value)
        get() = get(PREF_ACCEPT_GUIDE_SCREEN_DONT_SHOW_CHECKBOX, Boolean::class.java)

    var checkUncheckGuideScreenDontShowQuote: Boolean
        set(value) = put(PREF_ACCEPT_GUIDE_SCREEN_DONT_SHOW_CHECKBOX_Quote, value)
        get() = get(PREF_ACCEPT_GUIDE_SCREEN_DONT_SHOW_CHECKBOX_Quote, Boolean::class.java)

    var orderStatues: String
        set(value) = put(PREF_ORDER_STATUES, value)
        get() = get(PREF_ORDER_STATUES, String::class.java)

    var deliveryStatues: String
        set(value) = put(PREF_DELIVERY_STATUES, value)
        get() = get(PREF_DELIVERY_STATUES, String::class.java)


    init {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun <T> get(key: String, clazz: Class<T>): T =
        when (clazz) {
            String::class.java -> sharedPreferences.getString(key, "")
            Boolean::class.java -> sharedPreferences.getBoolean(key, false)
            Float::class.java -> sharedPreferences.getFloat(key, 0f)
            Double::class.java -> sharedPreferences.getFloat(key, 0f)
            Int::class.java -> sharedPreferences.getInt(key, 0)
            Long::class.java -> sharedPreferences.getLong(key, -1L)
            else -> null
        } as T

    private fun <T> put(key: String, data: T) {
        val editor = sharedPreferences.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Boolean -> editor.putBoolean(key, data)
            is Float -> editor.putFloat(key, data)
            is Double -> editor.putFloat(key, data.toFloat())
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
        }
        editor.apply()
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        const val PREF_SESSION_ID_NAVMAN = "pref_session_id_navman"
        const val PREFS_NAME = "AncoTurfSharedPreferences"
        private const val PREFIX = "AncoTurf_"
        const val FCM_TOKEN = PREFIX + "fcm_token"
        const val PREF_SESSION_ACCESS_TOKEN = PREFIX + "access_token"
        const val PREF_USER_ID = PREFIX + "user_id"
        const val PREF_USER_NAME = PREFIX + "user_name"
        const val PREF_USER_EMAIL = PREFIX + "user_email"
        const val PREF_USER_INFO = PREFIX + "user_info"
        const val PREF_IS_WELCOME_VISITED = PREFIX + "isWelcomeVisited"
        const val PREF_IS_LOGIN = PREFIX + "isLogin"
        const val PREF_USER_TYPE = PREFIX + "user_type"
        const val PREF_SIGNIN_INFO = PREFIX + "signin_info"
        const val PREF_SIGNUP_INFO = PREFIX + "signup_info"
        const val PREF_CONTACT_INFO = PREFIX + "contact_info"
        const val PREF_ONBOARDING_INFO = PREFIX + "onboarding_info"
        const val PREF_PRODUCT_UNITS = PREFIX + "product_units"
        const val PREF_TAGS = PREFIX + "tags"
        const val PREF_PRODUCT_MAX_PRICE = PREFIX + "product_max_price"
        const val PREF_QUOTE_MAX_PRICE = PREFIX + "quote_max_price"
        const val PREF_ORDER_MAX_PRICE = PREFIX + "order_max_price"
        const val PREF_MAX_PORTFOLIO_IMAGES = PREFIX + "max_portfolio_images"
        const val PREF_MAX_NUMBER_PORTFOLIOS = PREFIX + "max_number_portfolios"
        const val PREF_MAX_IMAGES_NON_ANCO_PRODUCTS_QUOTE =
            PREFIX + "max_images_non_anco_products_quote"
        const val PREF_DELIVERY_MIN_DAYS = PREFIX + "delivery_min_days"
        const val PREF_DELIVERY_MAX_DAYS = PREFIX + "delivery_max_days"
        const val PREF_AVAILABLE_CREDIT = PREFIX + "available_credit"
        const val PREF_TOTAL_PRODUCTS_IN_CART = PREFIX + "total_products_in_cart"
        const val PREF_DELIVERY_MESSAGE = PREFIX + "delivery_message"
        const val PREF_ORDER_STATUES = PREFIX + "order_statues"
        const val PREF_DELIVERY_STATUES = PREFIX + "delivery_statues"
        const val PREF_QUOTE_TO_ORDER_MESSAGE = PREFIX + "quote_to_order_message" //Dev_n
        const val PREF_DROP_TURF_ON_PROPERTY_TITLE = PREFIX + "drop_turf_on_property_title" //Dev_n
        const val PREF_DROP_TURF_ON_PROPERTY_DESCRIPTION =
            PREFIX + "drop_turf_on_property_description" //Dev_n
        const val PREF_ACCEPT_DECLINE_CHECKBOX_TITLE =
            PREFIX + "accept_decline_checkbox_title" // Dev_n

        const val PREF_ACCEPT_GUIDE_SCREEN_DONT_SHOW_CHECKBOX =
            PREFIX + "accept_guide_Screen_dont_show_checkbox" // Dev_n

         const val PREF_ACCEPT_GUIDE_SCREEN_DONT_SHOW_CHECKBOX_Quote =
            PREFIX + "accept_guide_Screen_dont_show_checkbox_Quote" // Dev_n

        const val PREF_CLICK_COLLECT_CHECKBOX_TITLE =
            PREFIX + "click_collect_checkbox_title" // Dev_n

         const val PREF_CLICK_COLLECT_CHECKBOX_DESCRIPTION =
            PREFIX + "click_collect_checkbox_description" // Dev_n

         const val PREF_CLICK_COLLECT_CHECKBOX_DESCRIPTION_TORQUAY =
            PREFIX + "torquy_click_collect_checkbox_description" // Dev_n

        const val PREF_QUOTE_ID = PREFIX + "PREF_QUOTE_ID"
        const val PREF_HOW_CAN_WE_HELP = PREFIX + "PREF_HOW_CAN_WE_HELP"
        const val PREF_HELP_CHOOSE_TURF = PREFIX + "PREF_HELP_CHOOSE_TURF"
        const val PREF_OPEN_HOME = PREFIX + "PREF_OPEN_HOME"
        const val PREF_ADMIN_FEE = PREFIX + "PREF_ADMIN_FEE"
        const val PREF_POSTAL_CODE = PREFIX + "PREF_POSTAL_CODE"  //nnn done 27/11/2020
        const val PREF_PHONE_NUMBER = PREFIX + "PREF_PHONE_NUMBER" //nnn done 27/11/2020
        const val PREF_IS_FIRST_CLICK_TURF = PREFIX + "PREF_IS_FIRST_CLICK_TURF"
    }
}