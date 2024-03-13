package com.jyn.pingtest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [UrlDetail::class], version = 1, exportSchema = false)
abstract class AppDatabase:RoomDatabase() {
    abstract fun urlDao():UrlDao

    companion object{
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "urldb")
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // 初始化数据
                            val initList = mutableListOf<UrlDetail>()
                            initList.add(UrlDetail(position = 1,url="www.vk.com"))
                            initList.add(UrlDetail(position = 2,url="detik.com"))
                            initList.add(UrlDetail(position = 3,url="www.baidu.com"))
                            getInstance(context).urlDao().insertUrlList(initList)
                        }
                    }
                )
                .build()
        }
    }
}