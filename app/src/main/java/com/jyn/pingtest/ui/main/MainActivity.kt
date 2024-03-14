package com.jyn.pingtest.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.jyn.pingtest.databinding.LayoutMainBinding
import com.jyn.pingtest.ui.add.AddUrlActivity
import com.jyn.pingtest.ui.views.ItemTouchDelegate
import com.jyn.pingtest.ui.views.ItemTouchHelperCallback
import com.jyn.pingtest.ui.views.ItemTouchHelperImpl
import com.jyn.pingtest.ui.views.SlideRecyclerView
import java.util.UUID

class MainActivity : ComponentActivity() {

    private lateinit var binding: LayoutMainBinding
    private lateinit var listViewModel: UrlListViewModel
    private lateinit var itemsAdapter: UrlAdapter
    private lateinit var itemTouchHelper: ItemTouchHelperImpl

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

    /**
     * 监听数据初始化逻辑，
     */
    private fun initDb() {
        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(UUID.fromString("5fc03087-d265-11e7-b8c6-83e29cd24f4c"))
            .observe(this) {
                if (it.state == WorkInfo.State.SUCCEEDED) {
                    listViewModel.pingAllUrl()
                }
            }
    }

    private fun initView() {
        itemsAdapter = UrlAdapter()
        binding.rlvItems.layoutManager = LinearLayoutManager(this)
        binding.rlvItems.adapter = itemsAdapter
        binding.rlvItems.setRightClickListener(object : SlideRecyclerView.OnRightClickListener {
            override fun onRightClick(position: Int) {
                //左滑删除操作
                itemsAdapter.removePositionItem(position)
                listViewModel.deleteItemByPosition(position)
            }
        })
        // 实现拖拽
        val itemTouchCallback = ItemTouchHelperCallback(object : ItemTouchDelegate {

            override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {
                //  if (mData.size > 1 && srcPosition < mData.size && targetPosition < mData.size) {
                // 更换数据源中的数据Item的位置
                // Collections.swap(mData, srcPosition, targetPosition)
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                itemsAdapter.notifyItemMoved(srcPosition, targetPosition)
                return true
                //    }
                //    return false
            }

            override fun uiOnDragging(viewHolder: RecyclerView.ViewHolder?) {
                viewHolder?.itemView?.setBackgroundColor(Color.parseColor("#22000000"))
            }

            override fun uiOnClearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                viewHolder.itemView.setBackgroundColor(Color.WHITE)
            }

        })

        itemTouchHelper = ItemTouchHelperImpl(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rlvItems)
        itemTouchCallback.setDragEnable(true)
        itemTouchCallback.setSwipeEnable(true)
    }

    private fun livedataObserve() {
        listViewModel.urlsLiveData.observe(this) {
            Log.d("jyntest", "列表更新" + it)
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

    //新增URL返回后的处理
    private var resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val url = result.data?.getStringExtra("ADD_URL")
                if (url.isNullOrEmpty()) {
                    return@registerForActivityResult
                }
                listViewModel.addNewUrl(url)
            }
        }
}