package com.jyn.pingtest.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jyn.pingtest.PingUtil
import com.jyn.pingtest.data.AppDatabase
import com.jyn.pingtest.data.UrlDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections


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

    /**
     * 新增一条新的URL
     */
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

    /**
     * 删除某条记录
     */
    fun deleteItemByPosition(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentList = urlsLiveData.value?.toMutableList()
            val deleteItem = urlsLiveData.value?.toMutableList()?.get(position)
            currentList?.let {
                it.removeAt(position)
                urlsLiveData.postValue(it)
            }
            deleteItem?.let {
                AppDatabase.getInstance(application).urlDao()
                    .deleteUrlItem(it)
            }
        }
    }

    /**
     * 更换元素中某个Item的位置
     * 此处只交换列表，不更新数据库
     */
    private var swapList = mutableListOf<UrlDetail>()
    fun swapItem(oldPosition: Int, targetPosition: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            if (swapList.isEmpty()) {
                urlsLiveData.value?.let {
                    swapList = it.toMutableList()
                }
            }
            if (targetPosition < 0 || targetPosition > swapList.size - 1) {
                return@launch
            }
            swapList.let {
                Collections.swap(it, oldPosition, targetPosition)
                swapList = it
            }
        }
    }

    fun swapEnd() {
        viewModelScope.launch(Dispatchers.IO) {
            swapList.let {
                it.forEachIndexed { index, urlDetail ->
                    urlDetail.position = index
                }
                AppDatabase.getInstance(application).urlDao().updateUrlList(it)
                swapList.clear()
            }
        }
    }

}