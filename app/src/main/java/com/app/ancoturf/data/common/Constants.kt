package com.app.ancoturf.data.common

import com.app.ancoturf.BuildConfig

object Constants {

    const val PAYMENT_DUE_TYPE: String="payment_due_type"
    const val FROM_GUEST_USER_REGISTRATION: String="from_guest_user_registration"
    const val REFERENCE_NUMBER: String="reference_number"
    const val ORDER_STATUS_CHANGED: String="ORDER_STATUS_CHANGE"
    const val TRACKING_FRAGMENT: String="TRACKING_FRAGMENT"
    const val IS_SIGN_UP_LANDSCAPER: String="IS_SIGN_UP_LANDSCAPER"
    const val SHOP_FRAGMENT: String="SHOP_FRAGMENT"
    const val CHOOSE_MY_LAWN: String="CHOOSE_MY_LAWN"
    const val MANAGE_MY_LAWN: String="MANAGE_MY_LAWN"
    const val NOTIFICATION_DATA: String = "NOTIFICATION_DATA"
    const val FROM_NOTIFICATION: String = "fromNotification"
    const val KEY_DEEP_LINKING: String = "DEEP_LINKING"
    const val KEY_QUOTE_STATUS: String = "QUOTE_STATUS"
    const val KEY_QUOTE_ID: String = "QUOTE_ID"
//    const val INVOICE_PDF: String = "https://ancob2b.sharptest.com.au/"
    const val INVOICE_PDF: String = "https://backend.ancoapp.com.au/"  //nnn 18/09/2020 //live

    const val QUOTE_ID_PARAM = "quoteId"
    const val ORDER_REFERENCE_NUMBER_PARAM: String="orderReferenceNo"
    const val QUOTE_STATUS_PARAM = "status"

    const val PRODUCT_ID_PARAM = "product_id"


    //user type
    const val GUEST_USER = 1
    const val RETAILER = 2
    const val WHOLESALER = 3
    const val LANDSCAPER = 4

    //portfolio modes
    const val ADD_PORTFOLIO = 1
    const val VIEW_PORTFOLIO = 2
    const val EDIT_PORTFOLIO = 3

    //quote modes
    const val ADD_QUOTE = 1
    const val VIEW_QUOTE = 2
    const val EDIT_QUOTE = 3
    const val DRAFT_QUOTE = 4;



    //payment methods
    const val COD = "cod"
    const val BANK_TRANSFER = "bank_transfer"
    const val AFTERPAY = "afterpay"  //paypal
    const val EXPRESS_CHECKOUT = "express_checkout"
    const val PAY_LATER = "pay_later"
    const val WESTPEC_PAY = "credit_card"

    const val COMPRESS_IMAGE_OVERRIDE_WIDTH = 600
    const val COMPRESS_IMAGE_OVERRIDE_HEIGHT = 200

    const val EXPRESS_CHECKOUT_LINK = BuildConfig.API_BASE_URL + "paypal?amount=%s"

    const val AFTERPAY_ERROR_MESSAGE = "Transaction get cancelled, Please try again"

    //shipping type
    const val DELIVER_TO_DOOR = "DeliverToDoor"
    const val CLICK_AND_COLLECT = "clickAndCollect"
    const val CLICK_AND_COLLECT_TORQUAY = "PickupFromTorquay"

    //shipping type
    const val DELIVER_TO_DOOR_LABEL = "Deliver To Door"
    const val CLICK_AND_COLLECT_LABEL = "Click And Collect"

    //order status
    const val ORDERED = "ordered"
    const val INVOICED = "invoiced"
    const val AWAITING_PAYMENT = "awaiting_payment"
    const val PAID = "paid"
    const val COMPLETED = "completed"
    const val CANCELLED = "cancelled"

    //delivery status
    const val ON_THE_WAY = "on_the_way"
    const val DELIVERED = "delivered"
    const val READY_FOR_PICK_UP = "ready_for_pick_up"
    const val PICKED_UP = "picked_up"
    const val FOR_PICK_UP = "for_pick_up"
    const val GUEST_USER_TO_LOGIN_VIA_CHECKOUT_ACTION = "guestUserToLoginViaCheckoutAction"

    const val MAX_NUMBER = 9999

    //shapes
    const val SQUARE = 1
    const val RECTANGLE = 2
    const val CIRCLE = 3
    const val TRIANGLE = 4

}