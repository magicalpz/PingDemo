package com.jyn.pingtest

import android.app.Application
import com.jyn.pingtest.data.AppDatabase

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        //创建数据库,并初始化默认数据
        AppDatabase.getInstance(applicationContext)
    }
}