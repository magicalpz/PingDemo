package com.jyn.pingtest.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jyn.pingtest.databinding.LayoutMainBinding
import com.jyn.pingtest.ui.add.AddUrlActivity

class MainActivity : ComponentActivity() {

    private lateinit var binding: LayoutMainBinding
    private lateinit var listViewModel: UrlListViewModel
    private lateinit var itemsAdapter: UrlAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        listViewModel = ViewModelProvider(this)[UrlListViewModel::class.java]
        initView()
        livedataObserve()
        addViewListener()
    }

    private fun initView() {
        itemsAdapter = UrlAdapter(mutableListOf())
        binding.rlvItems.layoutManager = LinearLayoutManager(this)
        binding.rlvItems.adapter = itemsAdapter
    }

    private fun livedataObserve() {
        listViewModel.urlsLiveData.observe(this) {
            itemsAdapter.setList(it)
        }
    }

    private fun addViewListener() {
        binding.btnRefresh.setOnClickListener {

        }
        binding.btnGotoAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddUrlActivity::class.java))
        }
    }
}