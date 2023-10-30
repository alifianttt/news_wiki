package com.submission.newsapp.data.source.remote.network

data class ApiResponse<out T>(val status: Status, val data: T?, val message: String?){
    companion object {
        fun <T> onSucces(data: T?) : ApiResponse<T> {
            return ApiResponse(Status.SUCCES, data, null)
        }

        fun <T> onError(msg: String?, data: T?): ApiResponse<T> {
            return ApiResponse(Status.ERROR, data, msg)
        }

        fun <T> onLoading(data: T?) : ApiResponse<T> {
            return ApiResponse(Status.LOADING, data, null)
        }
    }
}