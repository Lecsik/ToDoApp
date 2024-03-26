package ru.startandroid.todoapp.presentation.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.startandroid.todoapp.databinding.CheckBoxListItemBinding
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
        val binding: CheckBoxListItemBinding =
            CheckBoxListItemBinding.inflate(lInflater, parent, false)
        val holder = ItemListViewHolder(binding)
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

        holder.binding.checkBox.setOnCheckedChangeListener(null)
        holder.binding.checkBox.isChecked = todoItem.isCompleted
        holder.binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onClickListener?.onCheckChanged(
                holder.bindingAdapterPosition,
                items[holder.bindingAdapterPosition],
                isChecked
            )
        }
        holder.binding.itemText.text = todoItem.description
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

    class ItemListViewHolder(val binding: CheckBoxListItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
