package com.example.swapedemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var adapter: SwapeAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val layoutManager: RecyclerView.LayoutManager? =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        val modelList = readFromAsset();
        adapter = SwapeAdapter(modelList, this)
        recyclerView.adapter = adapter;

        // recyclerView.addItemDecoration(SimpleDividerItemDecoration(this))

        Swipe()

    }

    private fun Swipe() {
        val swipeToDeleteCallbackLeft =
            object : SwipeToDeleteCallback(this, 0, ItemTouchHelper.LEFT) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter!!.pendingRemoval(viewHolder.adapterPosition)
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    if (adapter!!.isPendingSwipe()) {
                        return 0
                    } else {
                        if (adapter!!.isPendingRemoval(viewHolder.adapterPosition)) {
                            return ItemTouchHelper.ACTION_STATE_IDLE
                        }
                        return super.getSwipeDirs(recyclerView, viewHolder)
                    }
                }
            }

        val itemTouchHelperLeft = ItemTouchHelper(swipeToDeleteCallbackLeft)
        itemTouchHelperLeft.attachToRecyclerView(recyclerView)

        val swipeToDeleteCallbackRight =
            object : SwipeToDeleteCallback(this, 0, ItemTouchHelper.RIGHT) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapter!!.pendingEditable(viewHolder.adapterPosition)
                }

                override fun getSwipeDirs(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    if (adapter!!.isPendingSwipe()) {
                        return 0
                    } else {
                        if (adapter!!.isPendingEdit(viewHolder.adapterPosition)) {
                            return ItemTouchHelper.ACTION_STATE_IDLE
                        }
                        return super.getSwipeDirs(recyclerView, viewHolder)
                    }
                }
            }

        val itemTouchHelperRight = ItemTouchHelper(swipeToDeleteCallbackRight)
        itemTouchHelperRight.attachToRecyclerView(recyclerView)
    }

    private fun readFromAsset(): ArrayList<String> {
        var arrayList: ArrayList<String> = ArrayList()
        for (i in 0..10) {
            arrayList.add("BCA: +$i")
        }
        return arrayList
    }
}