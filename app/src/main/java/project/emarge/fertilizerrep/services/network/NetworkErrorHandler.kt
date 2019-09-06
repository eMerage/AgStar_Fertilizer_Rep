package project.emarge.fertilizerrep.services.network

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import project.emarge.fertilizerrep.model.datamodel.NetworkError


import java.io.IOException
import java.net.SocketTimeoutException


class NetworkErrorHandler {
    var networkError : NetworkError = NetworkError()
    operator fun invoke(err: Throwable) : NetworkError{
        when (err) {
            is HttpException -> {
                networkError.errorMessage = err.message().toString()
                networkError.code = err.code()
                var error : String = when {
                    err.code() == 400 -> "Bad Request !"
                    err.code() == 401 -> "The request requires user authentication !"
                    err.code() == 403 -> "Forbidden , The server understood the request !"
                    err.code() == 404 -> "The server has not found anything matching the Request-URI !"
                    err.code() == 405 -> "The method specified in the Request-Line is not allowed !"
                    err.code() == 406 -> "Not Acceptable !"
                    err.code() == 407 -> "Proxy Authentication Required !"
                    err.code() == 408 -> "Request Timeout !"
                    err.code() == 409 -> "The request could not be completed due to a conflict !"
                    err.code() == 410 -> "The requested resource is no longer available at the server !"
                    err.code() == 411 -> "The server refuses to accept the request without a defined Content- Length !"
                    err.code() == 413 -> "Request Entity Too Large !"
                    err.code() == 414 -> "Request-URI Too Long !"
                    err.code() == 415 -> "Unsupported Media Type !"
                    err.code() == 417 -> "Expectation Failed !"
                    err.code() == 426-> "Upgrade Required !"
                    err.code() == 429 -> "Too Many Requests !"
                    err.code() == 431 -> "Request Header Fields Too Large !"
                    err.code() == 444 -> "No Response !"
                    err.code() == 499 -> "Client Closed Request !"
                    err.code() == 500 -> "Internal Server Error !"
                    err.code() == 501 -> "Not Implemented !"
                    err.code() == 502 -> "Bad Gateway !"
                    err.code() == 503 -> "Service Unavailable !"
                    err.code() == 504 -> "Gateway Timeout !"
                    err.code() == 505 -> "HTTP Version Not Supported !"
                    err.code() == 507 -> "Insufficient Storage !"
                    err.code() == 508 -> "Loop Detected !"
                    err.code() == 509 -> "Bandwidth Limit Exceeded !"
                    err.code() == 511 -> "Network Authentication Required !"
                    err.code() == 598 -> "Network read timeout error !"
                    err.code() == 599 -> "Network connect timeout error !"
                    else -> { "Error code not detected" }
                }
                networkError.errorTitle = error
            }
            is SocketTimeoutException -> {
                networkError.errorMessage = err.toString()
                networkError.errorTitle = "Time Out !"
            }
            is IOException -> {
                networkError.errorMessage = err.toString()
                networkError.errorTitle = "IOException !"
            }
            else -> {
                networkError.errorMessage = err.toString()
                networkError.errorTitle = "Error not detected !"
            }
        }
        return networkError
     }
}