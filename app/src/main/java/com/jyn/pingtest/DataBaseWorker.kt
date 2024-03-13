package com.jyn.pingtest

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jyn.pingtest.data.AppDatabase
import com.jyn.pingtest.data.UrlDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class DataBaseWorker(context: Context, workerParams: WorkerParameters): CoroutineWorker(context,workerParams) {
        override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val initList = mutableListOf<UrlDetail>()
            initList.add(UrlDetail(position = 0,url="www.vk.com"))
            initList.add(UrlDetail(position = 1,url="detik.com"))
            initList.add(UrlDetail(position = 2,url="www.baidu.com"))
            AppDatabase.getInstance(applicationContext).urlDao().insertUrlList(initList)
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }
}