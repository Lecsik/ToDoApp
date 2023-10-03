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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListViewHolder {
        val lInflater = LayoutInflater.from(parent.context)
        val holder = ItemListViewHolder(
            lInflater.inflate(R.layout.check_box_list_item, parent, false)
        )
        //val item = ////Error starts here "Cannot call this method while RecyclerView is computing a layout or scrolling"
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(
                holder.bindingAdapterPosition,
                items[holder.bindingAdapterPosition]
            )
        }
        holder.itemCheckBox.setOnCheckedChangeListener { _, isChecked ->
            onClickListener?.onCheckChanged(
                holder.bindingAdapterPosition,
                items[holder.bindingAdapterPosition],
                isChecked
            )
        }
        return holder
    }

    override fun onBindViewHolder(holder: ItemListViewHolder, position: Int) {
        holder.onBind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: TodoItem)
        fun onCheckChanged(position: Int, model: TodoItem, isChecked: Boolean)
    }

    class ItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)
        private val itemTextView: TextView = itemView.findViewById(R.id.item_text)

        fun onBind(todoItem: TodoItem) {
            itemCheckBox.isChecked = todoItem.isCompleted
            itemTextView.text = todoItem.description
        }
    }
}
