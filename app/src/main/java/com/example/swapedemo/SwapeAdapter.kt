package com.example.swapedemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.regular_row_item.view.*
import kotlinx.android.synthetic.main.swipe_right_item.view.*
import kotlinx.android.synthetic.main.swipe_row_item.view.*
import kotlinx.android.synthetic.main.swipe_row_item.view.undo

class SwapeAdapter(val modelList: ArrayList<String>, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var itemsPendingRemoval: MutableList<String>?
    private var itemsPendingEditable: MutableList<String>?
    var PENDING_REMOVAL_TIMEOUT: Long = 3000
    var pendingRunnables: HashMap<String, Runnable>? = HashMap()

    init {
        itemsPendingRemoval = mutableListOf<String>()
        itemsPendingEditable = mutableListOf<String>()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (itemsPendingRemoval!!.contains(modelList.get(position))) {
            //show swipe layout
            holder.itemView.swipeLayoutLeft.visibility = View.VISIBLE
            holder.itemView.swipeLayoutRight.visibility = View.GONE
            holder.itemView.regularLayout.visibility = View.GONE

            holder.itemView.undo.setOnClickListener({ view ->
                undoOpt(modelList.get(position), 1)
            })

        } else if (itemsPendingEditable!!.contains(modelList.get(position))) {
            //show swipe layout
            holder.itemView.swipeLayoutRight.visibility = View.VISIBLE
            holder.itemView.swipeLayoutLeft.visibility = View.GONE
            holder.itemView.regularLayout.visibility = View.GONE

        } else {
            //show regular layout
            holder.itemView.swipeLayoutLeft.visibility = View.GONE
            holder.itemView.swipeLayoutRight.visibility = View.GONE
            holder.itemView.regularLayout.visibility = View.VISIBLE

            holder.itemView.txt.text = modelList.get(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_row, parent, false))
    }


    override fun getItemCount(): Int {
        return modelList.size;
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    private fun undoOpt(model: String, isLeftSwipe: Int) {
        val pendingRemovalRunnable: Runnable? = pendingRunnables?.get(model)
        pendingRunnables?.remove(model)
        if (pendingRemovalRunnable != null)
            android.os.Handler().removeCallbacks(pendingRemovalRunnable)
        if (isLeftSwipe == 1) itemsPendingRemoval?.remove(model)
        else itemsPendingEditable?.remove(model)
        // this will rebind the row in "normal" state
        notifyItemChanged(modelList.indexOf(model))
    }


    fun pendingRemoval(position: Int) {

        val data = modelList.get(position)
        if (!itemsPendingRemoval!!.contains(data)) {
            itemsPendingRemoval?.add(data)
            // this will redraw row in "undo" state
            notifyItemChanged(position)
            // let's create, store and post a runnable to remove the data
            val pendingRemovalRunnable = Runnable {
                remove(modelList.indexOf(data))
            }

            android.os.Handler().postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT)
            // pendingRunnables!![data] = pendingRemovalRunnable
            pendingRunnables?.put(data, pendingRemovalRunnable)
        }
    }

    fun pendingEditable(position: Int) {

        val data = modelList.get(position)
        if (!itemsPendingEditable!!.contains(data)) {
            itemsPendingEditable?.add(data)
            // this will redraw row in "undo" state
            notifyItemChanged(position)
            // let's create, store and post a runnable to remove the data
            val pendingEditableRunnable = Runnable {
                undoOpt(data, 0)
            }

            android.os.Handler().postDelayed(pendingEditableRunnable, PENDING_REMOVAL_TIMEOUT)
            // pendingRunnables!![data] = pendingEditableRunnable
            pendingRunnables?.put(data, pendingEditableRunnable)
        }
    }

    fun remove(position: Int) {
        val data = modelList.get(position)
        if (itemsPendingRemoval!!.contains(data)) {
            itemsPendingRemoval?.remove(data)
        }
        if (modelList.contains(data)) {
            //dataList.remove(position)
            modelList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun isPendingRemoval(position: Int): Boolean {
        val data = modelList.get(position)
        return itemsPendingRemoval!!.contains(data)
    }

    fun isPendingSwipe(): Boolean {
        if (itemsPendingRemoval!!.size > 0 || itemsPendingEditable!!.size > 0) return true
        else return false

    }

    fun isPendingEdit(position: Int): Boolean {
        val data = modelList.get(position)
        return itemsPendingEditable!!.contains(data)
    }


}