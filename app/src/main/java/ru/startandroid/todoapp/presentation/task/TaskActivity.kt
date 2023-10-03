package ru.startandroid.todoapp.presentation.task

import android.app.DatePickerDialog
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.get
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import org.joda.time.LocalDate
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.TodoItem
import java.util.Calendar


class TaskActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get<TaskViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_screen)

        getExistedItem()?.let { viewModel.setExistingItem(it) }

        val priorityListItems = resources.getStringArray(R.array.priorityListItems)

        //toolbar
        findViewById<MaterialToolbar>(R.id.task_screen_toolbar).apply {
            setSupportActionBar(this)
            setNavigationIcon(R.drawable.property_1_close)
            setNavigationOnClickListener { finish() }
        }

        //description
        findViewById<TextInputEditText>(R.id.task_description).apply {
            this.addTextChangedListener { text ->
                viewModel.description.value = text.let { it!!.toString() }
            }

            viewModel.description.observe(this@TaskActivity) {
                if (it == text?.toString()) return@observe
                setText(it)
            }
        }

        //priority
        findViewById<AutoCompleteTextView>(R.id.priority).apply {
            val priorityAdapter = ArrayAdapter(
                this@TaskActivity,
                R.layout.priority_list,
                priorityListItems
            )

            setAdapter(priorityAdapter)
            addTextChangedListener { text ->
                viewModel.priority.value = when (text?.toString()) {
                    priorityListItems[2] -> TodoItem.Priority.HIGH
                    priorityListItems[1] -> TodoItem.Priority.LOW
                    priorityListItems[0] -> TodoItem.Priority.NONE
                    else -> TodoItem.Priority.NONE
                }
            }
            viewModel.priority.distinctUntilChanged().observe(this@TaskActivity) {
                setText(
                    when (it) {
                        TodoItem.Priority.HIGH -> priorityListItems[2]
                        TodoItem.Priority.LOW -> priorityListItems[1]
                        TodoItem.Priority.NONE -> priorityListItems[0]
                        else -> priorityListItems[0]
                    },
                    false
                )
            }
        }


        //pick DueDate
        findViewById<SwitchMaterial>(R.id.switch_date).apply {
            val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val calendar = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        this@TaskActivity,
                        { _, year, month, dayOfMonth ->
                            val date = LocalDate(year, month + 1, dayOfMonth)
                            viewModel.dueDate.value = date
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.setOnCancelListener { this.isChecked = false }
                    datePickerDialog.show()
                } else {
                    viewModel.dueDate.value = null
                }
            }
            setOnCheckedChangeListener(onCheckedChangeListener)

            //fill duedate field
            val selectDateText = this@TaskActivity.findViewById<TextView>(R.id.date_text)
            viewModel.dueDate.observe(this@TaskActivity) { incomDueDate ->
                setOnCheckedChangeListener(null)
                if (incomDueDate != null) {
                    selectDateText.text = incomDueDate.toString("dd MMMM yyyy")
                    isChecked = true
                } else {
                    selectDateText.text = ""
                    isChecked = false
                }
                setOnCheckedChangeListener(onCheckedChangeListener)
            }
        }

        // delete button
        findViewById<TextView>(R.id.delete_textView).apply {
            viewModel.isItemExists.observe(this@TaskActivity) { enabled ->
                isEnabled = enabled

                val color = if (enabled) R.color.color_red else R.color.label_disable
                setTextColor(ContextCompat.getColor(this@TaskActivity, color))
                compoundDrawablesRelative.forEach { drawable: Drawable? ->
                    drawable?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(this@TaskActivity, color),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

            setOnClickListener {
                viewModel.removeItem()
                finish()
            }
        }


    }

    private fun getExistedItem(): TodoItem? {
        return if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("item", TodoItem::class.java)
        } else intent.getParcelableExtra("item")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_task_screen_bar, menu)
        return true
    }

    // Menu - Save
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_text_button -> {
                viewModel.description.value?.takeIf { it.isNotBlank() }?.let {
                    viewModel.newItem()
                    finish()
                }
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
