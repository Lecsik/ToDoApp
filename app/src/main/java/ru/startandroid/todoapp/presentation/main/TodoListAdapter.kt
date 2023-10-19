package ru.startandroid.todoapp.presentation.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.TodoItem

class TodoListAdapter : RecyclerView.Adapter<TodoListAdapter.ItemListViewHolder>() {
    private var onClickListener: OnClickListener? = null
    var items: List<TodoItem> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val lInflater = LayoutInflater.from(parent.context)
        val holder = ItemListViewHolder(
            lInflater.inflate(R.layout.check_box_list_item, parent, false)
        )
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(
                holder.bindingAdapterPosition,
                items[holder.bindingAdapterPosition]
            )
        }
        return holder
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        val todoItem = items[position]

        holder.itemCheckBox.setOnCheckedChangeListener(null)
        holder.itemCheckBox.isChecked = todoItem.isCompleted
        holder.itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onClickListener?.onCheckChanged(
                holder.bindingAdapterPosition,
                items[holder.bindingAdapterPosition],
                isChecked
            )
        }
        holder.itemTextView.text = todoItem.description
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, model: TodoItem)
        fun onCheckChanged(position: Int, model: TodoItem, isChecked: Boolean)
    }

    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)
        val itemTextView: TextView = itemView.findViewById(R.id.item_text)
    }
}
