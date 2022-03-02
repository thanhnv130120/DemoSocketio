package com.example.demosocketio.ui.screen.home

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.demosocketio.data.model.Room
import com.example.demosocketio.data.response.DataResponse
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class HomeViewModel(val application: Application) : ViewModel() {

    var room: Room? = null
    val saveDoneLiveData = MutableLiveData<DataResponse<ValidateType>>()

    init {
        room = Room("","")
    }

    fun updateUserName(userName: String) {
        room?.userName = userName
    }

    fun updateRoomName(roomName: String) {
        room?.roomName = roomName
    }

    fun startChat() {
        viewModelScope.launch {
            val validateType = validate()
            saveDoneLiveData.value = DataResponse.DataSuccessResponse(validateType)
        }
    }

    private fun validate(): ValidateType {
        if (room!!.userName.isEmpty()) {
            return ValidateType.EmptyUserName
        } else {
            if (room!!.roomName.isEmpty()) {
                return ValidateType.EmptyRoomName
            }
        }
        return ValidateType.ValidateDone
    }

    enum class ValidateType {
        ValidateDone, EmptyUserName, EmptyRoomName
    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}