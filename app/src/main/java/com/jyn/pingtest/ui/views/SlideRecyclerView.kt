package com.jyn.pingtest.ui.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jyn.pingtest.ui.main.UrlAdapter
import kotlin.math.abs


class SlideRecyclerView : RecyclerView {
    private val TAG = "SlideRecyclerView"
    private var mTouchSlop: Int = 0
    private var xDown = 0
    private var yDown: Int = 0
    private var xMove: Int = 0
    private var yMove: Int = 0

    /**
     * 当前选中的item索引（这个很重要）
     */
    private var curSelectPosition = 0
    private var mScroller: Scroller? = null

    private var mCurItemLayout: ConstraintLayout? = null
    private var mLastItemLayout: ConstraintLayout? = null
    private var mLlHidden: TextView? = null //隐藏部分

    private var mItemDelete: TextView? = null

    /**
     * 隐藏部分长度
     */
    private var mHiddenWidth = 0

    /**
     * 记录连续移动的长度
     */
    private var mMoveWidth = 0

    /**
     * 是否是第一次touch
     */
    private var isFirst = true
    private var mContext: Context? = null

    /**
     * 删除的监听事件
     */
    private var mRightListener: OnRightClickListener? = null

    fun setRightClickListener(listener: OnRightClickListener?) {
        mRightListener = listener
    }


    constructor(context: Context) : this(context, null) {

    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {

        mContext = context
        //滑动到最小距离
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        //初始化Scroller
        mScroller = Scroller(context, LinearInterpolator(context, null))
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x.toInt()
        val y = e.y.toInt()
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                //记录当前按下的坐标
                xDown = x
                yDown = y
                //计算选中哪个Item
                val firstPosition =
                    (layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                val itemRect = Rect()
                val count = childCount
                var i = 0
                while (i < count) {
                    val child = getChildAt(i)
                    if (child.visibility == VISIBLE) {
                        child.getHitRect(itemRect)
                        if (itemRect.contains(x, y)) {
                            curSelectPosition = firstPosition + i
                            break
                        }
                    }
                    i++
                }
                if (isFirst) { //第一次时，不用重置上一次的Item
                    isFirst = false
                } else {
                    //屏幕再次接收到点击时，恢复上一次Item的状态
                    if (mLastItemLayout != null && mMoveWidth > 0) {
                        //将Item右移，恢复原位
                        scrollRight(mLastItemLayout, 0 - mMoveWidth)
                        //清空变量
                        mHiddenWidth = 0
                        mMoveWidth = 0
                    }
                }

                //取到当前选中的Item，赋给mCurItemLayout，以便对其进行左移
                val item = getChildAt(curSelectPosition - firstPosition)
                if (item != null) {
                    //获取当前选中的Item
                    val viewHolder: UrlAdapter.MyViewHolder =
                        getChildViewHolder(item) as UrlAdapter.MyViewHolder
                    mCurItemLayout = viewHolder.binding.root
                    //找到具体元素
                    mLlHidden = viewHolder.binding.delete
                    mItemDelete = viewHolder.binding.delete
                    mItemDelete?.setOnClickListener {
                        if (mRightListener != null) {
                            //删除
                            mRightListener?.onRightClick(curSelectPosition)
                        }
                    }

                    //这里将删除按钮的宽度设为可以移动的距离
                    mHiddenWidth = mLlHidden!!.width
                }
            }

            MotionEvent.ACTION_MOVE -> {
                xMove = x
                yMove = y
                val dx: Int = xMove - xDown //为负时：手指向左滑动；为正时：手指向右滑动。这与Android的屏幕坐标定义有关
                val dy: Int = yMove - yDown //
                //左滑
                if (dx < 0 && abs(dx.toDouble()) > mTouchSlop && abs(dy.toDouble()) < mTouchSlop) {
                    var newScrollX = abs(dx.toDouble()).toInt()
                    if (mMoveWidth >= mHiddenWidth) { //超过了，不能再移动了
                        newScrollX = 0
                    } else if (mMoveWidth + newScrollX > mHiddenWidth) { //这次要超了，
                        newScrollX = mHiddenWidth - mMoveWidth
                    }
                    //左滑，每次滑动手指移动的距离
                    scrollLeft(mCurItemLayout, newScrollX)
                    xDown = x
                    //对移动的距离叠加
                    mMoveWidth += newScrollX
                } else if (dx > 0) { //右滑
                    //执行右滑，这里没有做跟随，瞬间恢复
                    scrollRight(mCurItemLayout, 0 - mMoveWidth)
                    mMoveWidth = 0
                }
            }

            MotionEvent.ACTION_UP -> {
                val scrollX = mCurItemLayout!!.scrollX
                if (mHiddenWidth > mMoveWidth) {
                    val toX = mHiddenWidth - mMoveWidth
                    mMoveWidth = if (scrollX > mHiddenWidth / 3) { //超过一半长度时松开，则自动滑到左侧
                        scrollLeft(mCurItemLayout, toX)
                        mHiddenWidth
                    } else { //不到一半时松开，则恢复原状
                        scrollRight(mCurItemLayout, 0 - mMoveWidth)
                        0
                    }
                }
                mLastItemLayout = mCurItemLayout
            }
        }
        return super.onTouchEvent(e)
    }


    override fun computeScroll() {
        mScroller?.let {
            if (it.computeScrollOffset()) {
                mCurItemLayout?.scrollBy(it.currX, 0)
                invalidate()
            }
        }

    }

    /**
     * 向左滑动
     */
    private fun scrollLeft(item: View?, scorllX: Int) {
        item?.scrollBy(scorllX, 0)
    }

    /**
     * 向右滑动
     */
    private fun scrollRight(item: View?, scorllX: Int) {
        item?.scrollBy(scorllX, 0)
    }


    interface OnRightClickListener {
        fun onRightClick(position: Int)
    }

}