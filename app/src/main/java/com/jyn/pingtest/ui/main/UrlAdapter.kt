package com.jyn.pingtest.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jyn.pingtest.data.UrlDetail
import com.jyn.pingtest.databinding.LayoutItemUrlBinding
import com.jyn.pingtest.ui.main.UrlAdapter.MyViewHolder

class UrlAdapter() : RecyclerView.Adapter<MyViewHolder>() {
    private var items = mutableListOf<UrlDetail>()
    fun setList(urlItems: MutableList<UrlDetail>) {
        this.items = urlItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutItemUrlBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.urlContent.text = items[position].url
        holder.binding.urlSpeed.text = "${items[position].speed}ms"
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) 0 else items.size
    }

    class MyViewHolder(val binding: LayoutItemUrlBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}