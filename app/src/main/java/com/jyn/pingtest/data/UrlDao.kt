package com.jyn.pingtest.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UrlDao {

    @Insert
    fun insertUrl(item: UrlDetail)

    @Insert
    fun insertUrlList(items: List<UrlDetail>)

    @Delete
    fun deleteUrlItem(item: UrlDetail)

    @Query("select * from url_detail order by position asc")
    fun getAllUrlItems():MutableList<UrlDetail>
}