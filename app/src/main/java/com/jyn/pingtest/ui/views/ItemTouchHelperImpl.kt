package com.jyn.pingtest.ui.views

import androidx.recyclerview.widget.ItemTouchHelper

class ItemTouchHelperImpl(private val callback: ItemTouchHelperCallback) :
    ItemTouchHelper(callback) {

    fun setDragEnable(enable: Boolean) {
        callback.setDragEnable(enable)
    }

    fun setSwipeEnable(enable: Boolean) {
        callback.setSwipeEnable(enable)
    }
}