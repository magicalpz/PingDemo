package com.jyn.pingtest.ui.main

import android.app.Application
import android.os.Debug
import android.util.Log
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


     fun pingAllUrl() {

        viewModelScope.launch(Dispatchers.IO) {
            val initList = mutableListOf<UrlDetail>()
            initList.add(UrlDetail(position = 1,url="www.vk.com"))
            initList.add(UrlDetail(position = 2,url="detik.com"))
            initList.add(UrlDetail(position = 3,url="www.baidu.com"))
            AppDatabase.getInstance(application).urlDao().insertUrlList(initList)
            val urls = AppDatabase.getInstance(getApplication()).urlDao().getAllUrlItems()
            Log.d("jyntest","当前列表"+urls.size)
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
            Log.d("jyntest","刷新列表"+newData.size)
            urlsLiveData.postValue(newData)
        }
    }

    private suspend fun getPingResult(urlDetail: UrlDetail): UrlDetail {
      val speed =  com.jyn.pingtest.PingUtil.ping(urlDetail.url)
        urlDetail.speed = speed
        return urlDetail
    }

}