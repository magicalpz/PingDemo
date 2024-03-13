package com.jyn.pingtest.ui.add

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.jyn.pingtest.R

class AddUrlActivity:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_add_url)
    }
}