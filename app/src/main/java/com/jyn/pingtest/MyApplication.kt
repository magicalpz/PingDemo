package com.jyn.pingtest

import android.app.Application
import android.content.SharedPreferences
import androidx.work.Configuration
import com.jyn.pingtest.data.AppDatabase


class MyApplication:Application(),Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        //创建数据库
        AppDatabase.getInstance(applicationContext)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .build()
    }
}