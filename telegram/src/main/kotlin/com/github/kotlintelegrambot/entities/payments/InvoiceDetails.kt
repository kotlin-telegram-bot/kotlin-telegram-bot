package com.github.kotlintelegrambot.entities.payments

/**
 * @property [needName] Pass True, if you require the user's full name to complete the order.
 * @property [needPhoneNumber] Pass True, if you require the user's phone number to complete the order.
 * @property [needEmail] Pass True, if you require the user's email to complete the order.
 * @property [needShippingAddress] Pass True, if you require the user's shipping address to complete the order.
 * @property [sendPhoneNumberToProvider] Pass True, if user's phone number should be sent to provider.
 * @property [sendEmailToProvider] Pass True, if user's email address should be sent to provider.
 * @constructor Create object which describe needed info about user for success payment
 */
data class InvoiceUserDetail(
    val needName: Boolean? = null,
    val needPhoneNumber: Boolean? = null,
    val needEmail: Boolean? = null,
    val needShippingAddress: Boolean? = null,
    val sendPhoneNumberToProvider: Boolean? = null,
    val sendEmailToProvider: Boolean? = null
)

/**
 * @property [photoUrl] URL of the product photo for the invoice. Can be a photo of the goods or a marketing image for a service. People like it better when they see what they are paying for.
 * @property [photoSize] Photo size.
 * @property [photoWidth] Photo width.
 * @property [photoHeight] Photo height.
 * @constructor Create object to show photo image for invoice
 */
class InvoicePhotoDetail private constructor(
    val photoUrl: String? = null,
    val photoSize: Int? = null,
    val photoWidth: Int? = null,
    val photoHeight: Int? = null
) {

    /**
     * @property [photoUrl] URL of the product photo for the invoice. Can be a photo of the goods or a marketing image for a service. People like it better when they see what they are paying for.
     * @property [photoSize] Photo size.
     */
    constructor(
        photoUrl: String,
        photoSize: Int
    ) : this(photoUrl, photoSize, null, null)

    /**
     * @param [photoUrl] URL of the product photo for the invoice. Can be a photo of the goods or a marketing image for a service. People like it better when they see what they are paying for.
     * @param [photoWidth] Photo width.
     * @param [photoHeight] Photo height.
     */
    constructor(
        photoUrl: String,
        photoWidth: Int,
        photoHeight: Int
    ) : this(photoUrl, null, photoWidth, photoHeight)
}
