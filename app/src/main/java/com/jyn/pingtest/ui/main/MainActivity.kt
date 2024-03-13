package com.jyn.pingtest.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.jyn.pingtest.DataBaseWorker
import com.jyn.pingtest.PingUtil
import com.jyn.pingtest.databinding.LayoutMainBinding
import com.jyn.pingtest.ui.add.AddUrlActivity
import java.util.UUID

class MainActivity : ComponentActivity() {

    private lateinit var binding: LayoutMainBinding
    private lateinit var listViewModel: UrlListViewModel
    private lateinit var itemsAdapter: UrlAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        listViewModel = ViewModelProvider(this)[UrlListViewModel::class.java]
        livedataObserve()
        initDb()
        initView()
        addViewListener()
        listViewModel.pingAllUrl()
    }

    private fun initDb() {
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(UUID.fromString("5fc03087-d265-11e7-b8c6-83e29cd24f4c")).observe(this){
                if (it.state == WorkInfo.State.SUCCEEDED){
                    listViewModel.pingAllUrl()
                }
            }
    }

    private fun initView() {
        itemsAdapter = UrlAdapter()
        binding.rlvItems.layoutManager = LinearLayoutManager(this)
        binding.rlvItems.adapter = itemsAdapter
    }

    private fun livedataObserve() {
        listViewModel.urlsLiveData.observe(this) {
            itemsAdapter.setList(it?.toMutableList() ?: mutableListOf())
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun addViewListener() {
        binding.btnRefresh.setOnClickListener {
            listViewModel.pingAllUrl()
        }
        binding.btnGotoAdd.setOnClickListener {
            resultLauncher.launch(Intent(this@MainActivity, AddUrlActivity::class.java))
        }
    }

    private var resultLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if (result.resultCode == Activity.RESULT_OK){
            val url = result.data?.getStringExtra("ADD_URL")
            if (url.isNullOrEmpty()){
                return@registerForActivityResult
            }
            listViewModel.addNewUrl(url)
        }
    }
}