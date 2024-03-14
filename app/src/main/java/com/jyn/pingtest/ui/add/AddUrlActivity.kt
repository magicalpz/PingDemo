package com.jyn.pingtest.ui.add

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import com.jyn.pingtest.databinding.LayoutAddUrlBinding

class AddUrlActivity : ComponentActivity() {

    private lateinit var binding: LayoutAddUrlBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusBarSet()
        binding = LayoutAddUrlBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        addViewListener()
    }

    private fun statusBarSet() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
    }

    private fun addViewListener() {
        binding.btnAdd.setOnClickListener {
            saveUrlAndBackHomeView()
        }
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * 保存url，并返回主界面
     */
    private fun saveUrlAndBackHomeView() {
        val inputUlr = binding.inputContent.text.toString()
        if (inputUlr.isEmpty()) {
            Toast.makeText(this, "域名不能为空", Toast.LENGTH_SHORT).show()
            return
        }
        val regex =
            "^(?=^.{3,255}\$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\$".toRegex()
        val match = regex.matches(inputUlr)
        if (!match) {
            Toast.makeText(this, "域名格式不正确", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK, intent.putExtra("ADD_URL", inputUlr))
        finish()
    }
}