package com.jyn.pingtest.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "url_detail")
data class UrlDetail(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var position: Int,
    var url: String,
    @Ignore var speed: Int = 0,
) {

}
