package com.github.kotlintelegrambot.entities.payments

/**
 * Payment invoice information
 *
 * Contains all needed information to create payment invoice
 *
 * @param [title] Product name.
 * @param [description] Product description.
 * @param [payload] Bot-defined invoice payload, 1-128 bytes. This will not be displayed to the user, use for your internal processes.
 * @param [providerToken] Payments provider token, obtained via Botfather.
 * @param [startParameter] Unique deep-linking parameter that can be used to generate this invoice when used as a start parameter.
 * @param [currency] Three-letter ISO 4217 currency code.
 * @param [prices] Price breakdown, a list of components (e.g. product price, tax, discount, delivery cost, delivery tax, bonus, etc.).
 * @param [isFlexible] Pass True, if the final price depends on the shipping method.
 * @see InvoicePhotoDetail
 * @see InvoiceUserDetail
 */
data class PaymentInvoiceInfo(
    val title: String,
    val description: String,
    val payload: String,
    val providerToken: String,
    val startParameter: String,
    val currency: String,
    val prices: List<LabeledPrice>,
    val isFlexible: Boolean? = null,
    val providerData: String? = null,
    val invoicePhoto: InvoicePhotoDetail? = null,
    val invoiceUserDetail: InvoiceUserDetail? = null
)
