package ru.startandroid.todoapp.presentation.main

//import android.widget.SimpleAdapter
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.TodoItem
import ru.startandroid.todoapp.presentation.task.TaskActivity


class MainActivity : AppCompatActivity() {


    private val toDoListAdapter = TodoListAdapter()//.apply { items = this@MainActivity.items }
    private lateinit var executeTitle: TextView

    private val viewModel by lazy { ViewModelProvider(this).get<MainViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lvMain = findViewById<RecyclerView>(R.id.items_list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        lvMain.adapter = toDoListAdapter
        lvMain.layoutManager = layoutManager

        executeTitle = findViewById<TextView>(R.id.executed_title)
        //Toolbar visibility button
        val visibilityButton = findViewById<ImageButton>(R.id.visibilityButton)
        visibilityButton.setOnClickListener { viewModel.switchCompletedTasksVisibility() }
        viewModel.isCompletedTasksVisible.observe(this) { isCompletedTasksVisible ->
            visibilityButton.setImageResource(
                if (isCompletedTasksVisible) R.drawable.visibility_off
                else R.drawable.visibility
            )
        }

        viewModel.items.observe(this) {
            lvMain.post { toDoListAdapter.items = it }
        }

        viewModel.count.observe(this) {
            executeTitle.text = getString(R.string.executed_title, it)
        }

        //On swipe
        val background = ColorDrawable()
        val swipeRightIcon =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.property_1_delete)!!
        val swipeLeftIcon =
            ContextCompat.getDrawable(this@MainActivity, R.drawable.property_1_check)!!
        val intrinsicHeightRight = swipeRightIcon.intrinsicHeight
        val intrinsicWidthRight = swipeRightIcon.intrinsicWidth
        val intrinsicHeightLeft = swipeLeftIcon.intrinsicHeight
        val intrinsicWidthLeft = swipeLeftIcon.intrinsicWidth

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top

                if (dX < 0) {
                    background.color = ContextCompat.getColor(this@MainActivity, R.color.color_red)
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)
                    val iconTop = itemView.top + (itemHeight - intrinsicHeightRight) / 2
                    val iconMargin = (itemHeight - intrinsicHeightRight) / 2
                    val iconLeft = itemView.right - iconMargin - intrinsicWidthRight
                    val iconRight = itemView.right - iconMargin
                    val iconBottom = iconTop + intrinsicHeightRight
                    swipeRightIcon.bounds.set(iconLeft, iconTop, iconRight, iconBottom)
                    swipeRightIcon.setTint(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.color_white
                        )
                    )
                    swipeRightIcon.draw(c)
                } else {
                    background.color =
                        ContextCompat.getColor(this@MainActivity, R.color.color_green)
                    background.setBounds(
                        itemView.left + dX.toInt(),
                        itemView.top,
                        itemView.left,
                        itemView.bottom
                    )
                    background.draw(c)
                    val iconTop = itemView.top + (itemHeight - intrinsicHeightLeft) / 2
                    val iconMargin = (itemHeight - intrinsicHeightLeft) / 2
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + iconMargin + intrinsicWidthLeft
                    val iconBottom = iconTop + intrinsicHeightLeft
                    swipeLeftIcon.bounds.set(iconLeft, iconTop, iconRight, iconBottom)
                    swipeLeftIcon.setTint(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.color_white
                        )
                    )
                    swipeLeftIcon.draw(c)
                }
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY,
                    actionState, isCurrentlyActive
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                //delete item here
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.removeItem(position)
                } else {
                    viewModel.setCompleted(position, true)
                }
            }

        }
        )
        itemTouchHelper.attachToRecyclerView(lvMain)

        //FloatingActionBar - go to TaskScreen
        val fab: View = findViewById(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, TaskActivity::class.java)
            startActivity(intent)
        }

        toDoListAdapter.setOnClickListener(object :
            TodoListAdapter.OnClickListener {
            override fun onClick(position: Int, model: TodoItem) {
                val intent = Intent(this@MainActivity, TaskActivity::class.java)
                intent.putExtra("item", model)
                startActivity(intent)
            }

            override fun onCheckChanged(position: Int, model: TodoItem, isChecked: Boolean) {
                viewModel.setCompleted(position, isChecked)
            }
        })

    }
}

