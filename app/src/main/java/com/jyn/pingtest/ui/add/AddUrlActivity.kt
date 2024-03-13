package com.jyn.pingtest.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.jyn.pingtest.databinding.LayoutAddUrlBinding

class AddUrlActivity : ComponentActivity() {

    private lateinit var binding: LayoutAddUrlBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutAddUrlBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        addViewListener()
    }

    private fun addViewListener() {
        binding.btnAdd.setOnClickListener {
            saveUrlAndBackHomeView()
        }
    }

    /**
     * 保存url，并返回主界面
     */
    private fun saveUrlAndBackHomeView() {
        val inputUlr = binding.inputContent.text
        if (inputUlr.isNullOrEmpty()) {
            Toast.makeText(this, "请输入域名", Toast.LENGTH_SHORT).show()
            return
        }
    }
}