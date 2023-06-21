package com.gquesada.notes.ui.tag.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.gquesada.notes.R
import com.gquesada.notes.ui.tag.models.UITag

class TagListAdapter(
    private val onTagSelected: (uiTag: UITag) -> Unit
) : RecyclerView.Adapter<TagListAdapter.TagViewHolder>() {
    private val data = mutableListOf<UITag>()

    fun setData(dataSource: List<UITag>) {
        data.clear()
        data.addAll(dataSource)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tag_item_layout, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(data[position], onTagSelected)
    }


    class TagViewHolder(view: View) : ViewHolder(view) {

        fun bind(tag: UITag, onTagSelected: (uiTag: UITag) -> Unit) {
            with(itemView) {
                findViewById<CheckBox>(R.id.cb_tag_isChecked).apply {
                    isChecked = tag.isChecked
                    setOnClickListener {
                        onTagSelected(tag)
                    }
                }
                findViewById<TextView>(R.id.tv_tag_name).text = tag.name
            }
        }
    }
}
