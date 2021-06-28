package com.ufps.geduco.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ufps.geduco.data.DataRepository
import com.ufps.geduco.data.model.Assistance
import com.ufps.geduco.data.model.Course
import com.ufps.geduco.data.model.User
import com.ufps.geduco.data.model.WorkingDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {

    private val _user = MutableLiveData<Event<User>>()
    private val _message = MutableLiveData<Event<String>>()
    private val _isLoading = MutableLiveData<Event<Boolean>>()
    private val _courses = MutableLiveData<Event<List<Course>>>()
    private val _workingsDays = MutableLiveData<Event<List<WorkingDay>>>()
    private val _assistance = MutableLiveData<Event<Assistance>>()
    private val _valueQr = MutableLiveData<Event<String>>()

    val user: LiveData<Event<User>> = _user
    val message: LiveData<Event<String>> = _message
    val isLoading: LiveData<Event<Boolean>> = _isLoading
    val courses: LiveData<Event<List<Course>>> = _courses
    val workingsDays: LiveData<Event<List<WorkingDay>>> = _workingsDays
    val assistance: LiveData<Event<Assistance>> = _assistance
    val valueQr: LiveData<Event<String>> = _valueQr

    fun verifyUser(token: String?) {
        _isLoading.postValue(Event(true))
        viewModelScope.launch {
            val res = dataRepository.verifyUser(token)
            if (res.data != null) {
                _user.postValue(Event(res.data))
            } else {
                emitMessage(res.message)
            }
            _isLoading.postValue(Event(false))
        }
    }

    fun getCourses(idUser: Int, progress: Boolean = true) {
        if (progress) {
            _isLoading.postValue(Event(true))
        }
        viewModelScope.launch {
            val res = dataRepository.getCourses(idUser)
            _courses.postValue(Event(res.data!!))
            emitMessage(res.message)
            _isLoading.postValue(Event(false))
        }
    }

    fun getWorkingDays(idEdu: Int) {
        _isLoading.postValue(Event(true))
        viewModelScope.launch {
            val res = dataRepository.getWorkingDays(idEdu)
            _workingsDays.postValue(Event(res.data!!))
            emitMessage(res.message)
            _isLoading.postValue(Event(false))
        }
    }

    fun checkAttendance(idEdu: Int, idWorkingDay: Int, qr: String) {
        _isLoading.postValue(Event(true))
        viewModelScope.launch {
            val res = dataRepository.checkAttendance(idEdu, idWorkingDay, qr)
            if (res.data != null) {
                _valueQr.postValue(Event(qr))
                _assistance.postValue(Event(res.data))
            } else {
                _valueQr.postValue(Event(""))
                emitMessage(res.message)
            }
            _isLoading.postValue(Event(false))
        }
    }

    private fun emitMessage(msg: String?) {
        msg?.let {
           _message.postValue(Event(it))
        }
    }
}
