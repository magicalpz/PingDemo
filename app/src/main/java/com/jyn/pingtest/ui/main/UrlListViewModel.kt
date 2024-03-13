package com.jyn.pingtest.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jyn.pingtest.data.AppDatabase
import com.jyn.pingtest.data.UrlDetail
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UrlListViewModel(private val application: Application) : AndroidViewModel(application) {

     var urlsLiveData: MutableLiveData<List<UrlDetail>> = MutableLiveData()

    init {
        pingAllUrl()
    }

    private fun pingAllUrl() {
        viewModelScope.launch(Dispatchers.IO) {
            val urls = AppDatabase.getInstance(getApplication()).urlDao().getAllUrlItems()
            val listDeferred = mutableListOf<Deferred<UrlDetail>>()
            urls.forEach {
                val deferred = viewModelScope.async {
                    getPingResult(it)
                }
                listDeferred.add(deferred)
            }
            val newData: MutableList<UrlDetail> = mutableListOf()
            listDeferred.forEach {
                newData.add(it.await())
            }
            urlsLiveData.postValue(newData)
        }

    }

    private suspend fun getPingResult(urlDetail: UrlDetail): UrlDetail {
        delay(1000L)
        urlDetail.speed = 10
        return urlDetail
    }

}