package com.github.kotlintelegrambot.network

import retrofit2.Response

/**
 * The type of the execution result of our api client calls is currently represented by the type
 * [retrofit2.Response] from the retrofit library and another internal type called
 * [com.github.kotlintelegrambot.network.Response] as its type parameter. As you can imagine, this
 * is very confusing because we end up with something like Response<Response<T>>. I'm creating this
 * typealias in order to better differentiate the Response types we're using there.
 */
internal typealias CallResponse<T> = Response<T>
