package com.vlad.ege_math

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(
    private val recyclerViewItems: ArrayList<String>,
    private val recyclerViewActivity: RecyclerViewActivity) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, private val recyclerViewActivity: RecyclerViewActivity)
        : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.itemTextView)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    recyclerViewActivity.goToActivity(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view,recyclerViewActivity)

    }

    override fun getItemCount(): Int {
        return recyclerViewItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: String = recyclerViewItems[position]
        holder.textView.text = item
    }
}