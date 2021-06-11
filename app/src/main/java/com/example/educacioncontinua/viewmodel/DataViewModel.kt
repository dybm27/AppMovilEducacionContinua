package com.example.educacioncontinua.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.educacioncontinua.model.DataRepository
import com.example.educacioncontinua.model.data.Assistance
import com.example.educacioncontinua.model.data.Course
import com.example.educacioncontinua.model.data.User
import com.example.educacioncontinua.model.data.WorkingDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(private val dataRepository: DataRepository) : ViewModel() {

    private val user = MutableLiveData<Event<User>>()
    private val message = MutableLiveData<Event<String>>()
    private val isLoading = MutableLiveData<Event<Boolean>>()
    private val courses = MutableLiveData<Event<List<Course>>>()
    private val workingsDays = MutableLiveData<Event<List<WorkingDay>>>()
    private val assistance = MutableLiveData<Event<Assistance>>()
    private val valueQr = MutableLiveData<Event<String>>()

    fun getUser(): LiveData<Event<User>> {
        return user
    }

    fun getMessage(): LiveData<Event<String>> {
        return message
    }

    fun isLoading(): LiveData<Event<Boolean>> {
        return isLoading
    }

    fun getCourses(): LiveData<Event<List<Course>>> {
        return courses
    }

    fun getWorkingsDays(): LiveData<Event<List<WorkingDay>>> {
        return workingsDays
    }

    fun getAssistance(): LiveData<Event<Assistance>> {
        return assistance
    }

    fun getValueQr(): LiveData<Event<String>> {
        return valueQr
    }

    fun verifyUser(token: String?) {
        isLoading.postValue(Event(true))
        viewModelScope.launch {
            val res = dataRepository.verifyUser(token)
            if (res.data != null) {
                user.postValue(Event(res.data))
            } else {
                emitMessage(res.message)
            }
            isLoading.postValue(Event(false))
        }
    }

    fun getCourses(idUser: Int, progress: Boolean = true) {
        if (progress) {
            isLoading.postValue(Event(true))
        }
        viewModelScope.launch {
            val res = dataRepository.getCourses(idUser)
            courses.postValue(Event(res.data!!))
            emitMessage(res.message)
            isLoading.postValue(Event(false))
        }
    }

    fun getWorkingDays(idEdu: Int) {
        isLoading.postValue(Event(true))
        viewModelScope.launch {
            val res = dataRepository.getWorkingDays(idEdu)
            workingsDays.postValue(Event(res.data!!))
            emitMessage(res.message)
            isLoading.postValue(Event(false))
        }
    }

    fun checkAttendance(idEdu: Int, idWorkingDay: Int, qr: String) {
        isLoading.postValue(Event(true))
        viewModelScope.launch {
            val res = dataRepository.checkAttendance(idEdu, idWorkingDay, qr)
            if (res.data != null) {
                valueQr.postValue(Event(qr))
                assistance.postValue(Event(res.data))
            } else {
                valueQr.postValue(Event(""))
                emitMessage(res.message)
            }
            isLoading.postValue(Event(false))
        }
    }

    private fun emitMessage(msg: String?) {
        msg?.let {
            message.postValue(Event(it))
        }
    }
}
