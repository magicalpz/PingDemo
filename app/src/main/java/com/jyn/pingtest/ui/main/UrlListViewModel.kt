package com.jyn.pingtest.ui.main

import android.app.Application
import android.os.Debug
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jyn.pingtest.PingUtil
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
            urlsLiveData.postValue(urls)
            urls.forEachIndexed { index, urlDetail ->
                viewModelScope.launch(Dispatchers.IO) {
                    val speed = PingUtil.ping(urlDetail.url)
                    urlDetail.speed = speed
                    urlsLiveData.postValue(urls)
                }
            }
        }
    }

    fun addNewUrl(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val urlDetail = UrlDetail(url = url, position = urlsLiveData.value?.size ?: 0)
            val id = AppDatabase.getInstance(application).urlDao().insertUrl(urlDetail)
            urlDetail.id = id.toInt()
            val currentList: MutableList<UrlDetail> =
                urlsLiveData.value?.toMutableList() ?: mutableListOf()
            currentList.add(urlDetail)
            urlsLiveData.postValue(currentList)
            val speed = PingUtil.ping(urlDetail.url)
            urlDetail.speed = speed
            urlsLiveData.postValue(currentList)
        }
    }


    fun deleteItemByPosition(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("jyntest", "位置 " + position)
            val currentList = urlsLiveData.value?.toMutableList()
            val deleteItem = urlsLiveData.value?.toMutableList()?.get(position)
            currentList?.let {
                it.removeAt(position)
                urlsLiveData.postValue(it)
            }

            Log.d("jyntest", "删除的条目 " + deleteItem)
            deleteItem?.let {
                AppDatabase.getInstance(application).urlDao()
                    .deleteUrlItem(it)
            }
        }
    }

}