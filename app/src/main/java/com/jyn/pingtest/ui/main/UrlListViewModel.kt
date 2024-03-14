package com.jyn.pingtest.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jyn.pingtest.data.AppDatabase
import com.jyn.pingtest.data.UrlDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UrlListViewModel(private val application: Application) : AndroidViewModel(application) {


    var urlsLiveData: MutableLiveData<List<UrlDetail>> = MutableLiveData()

    /**
     * 获取所有列表的延迟
     * 单位ms
     */
    fun pingAllUrl() {
        viewModelScope.launch(Dispatchers.IO) {
            val urls = AppDatabase.getInstance(getApplication()).urlDao().getAllUrlItems()
            Log.d("jyntest", "列表大小 " + urls.size)
            urlsLiveData.postValue(urls)
            urls.forEach {
                viewModelScope.launch(Dispatchers.IO) {
                    val newData = getPingResult(it)
                    urlsLiveData.value?.toMutableList()?.set(newData.position, newData)
                    urlsLiveData.postValue(urlsLiveData.value)
                }
            }
        }
    }

    fun addNewUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val urlDetail = UrlDetail(url = url, position = urlsLiveData.value?.size ?: 0)
            AppDatabase.getInstance(application).urlDao().insertUrl(urlDetail)
            val currentList: MutableList<UrlDetail> =
                urlsLiveData.value?.toMutableList() ?: mutableListOf()
            currentList.add(urlDetail)
            urlsLiveData.postValue(currentList)
            val newData = getPingResult(urlDetail)
            urlsLiveData.value?.toMutableList()?.set(newData.position, newData)
            urlsLiveData.postValue(urlsLiveData.value)
        }
    }

    private fun getPingResult(urlDetail: UrlDetail): UrlDetail {
        val speed = com.jyn.pingtest.PingUtil.ping(urlDetail.url)
        urlDetail.speed = speed
        return urlDetail
    }

}