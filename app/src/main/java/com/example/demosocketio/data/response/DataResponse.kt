package com.example.demosocketio.data.response

import com.example.demosocketio.data.model.LoadDataStatus


sealed class DataResponse<T> constructor(val loadDataStatus: LoadDataStatus) {
    class DataEmptyResponse<T> : DataResponse<T>(LoadDataStatus.IDLE)
    class DataErrorResponse<T> : DataResponse<T>(LoadDataStatus.ERROR)
    data class DataSuccessResponse<T>(val body: T) : DataResponse<T>(LoadDataStatus.SUCCESS)
    class DataLoadingResponse<T> : DataResponse<T>(LoadDataStatus.LOADING)
}