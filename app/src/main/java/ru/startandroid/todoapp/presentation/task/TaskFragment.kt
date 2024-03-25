package ru.startandroid.todoapp.presentation.task

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.get
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import org.joda.time.LocalDate
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.models.TodoItem
import ru.startandroid.todoapp.presentation.main.MainActivity
import java.util.Calendar

class TaskFragment : Fragment(R.layout.fragment_task) {
    private val viewModel by lazy { ViewModelProvider(this).get<TaskViewModel>() }
    private var resultIntent: Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        getExistedItem()?.let { viewModel.setExistingItem(it) }

        val priorityListItems = resources.getStringArray(R.array.priorityListItems)

        //toolbar
        view.findViewById<MaterialToolbar>(R.id.task_screen_toolbar).apply {
            setNavigationIcon(R.drawable.property_1_close)
            inflateMenu(R.menu.top_app_task_screen_bar)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.save_text_button -> {
                        viewModel.description.value?.takeIf { it.isNotBlank() }?.let {
                            resultIntent = Intent(this.context, MainActivity::class.java).apply {
                                putExtra("newItem", viewModel.save())
                            }
                        }
                        true
                    }

                    else -> super.onOptionsItemSelected(it)
                }
            }
            setNavigationOnClickListener { requireActivity().finish() }
        }

        //description
        view.findViewById<TextInputEditText>(R.id.task_description).apply {
            this.addTextChangedListener { text ->
                viewModel.description.value = text.let { it!!.toString() }
            }

            viewModel.description.observe(viewLifecycleOwner) {
                if (it == text?.toString()) return@observe
                setText(it)
            }
        }

        //priority
        view.findViewById<AutoCompleteTextView>(R.id.priority).apply {
            val priorityAdapter = ArrayAdapter(
                context,
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
            viewModel.priority.distinctUntilChanged().observe(viewLifecycleOwner) {
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
        view.findViewById<SwitchMaterial>(R.id.switch_date).apply {
            val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val calendar = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        context,
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
            val selectDateText = view.findViewById<TextView>(R.id.date_text)
            viewModel.dueDate.observe(viewLifecycleOwner) { incomDueDate ->
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
        view.findViewById<TextView>(R.id.delete_textView).apply {
            viewModel.isItemExists.observe(viewLifecycleOwner) { enabled ->
                isEnabled = enabled

                val color = if (enabled) R.color.color_red else R.color.label_disable
                setTextColor(ContextCompat.getColor(context, color))
                compoundDrawablesRelative.forEach { drawable: Drawable? ->
                    drawable?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(context, color),
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

            setOnClickListener {
                resultIntent = Intent(context, MainActivity::class.java).apply {
                    putExtra("deleteItem", viewModel.remove())
                }
            }
        }

        //progressBar
        val progressBarDialog = ProgressDialog(context).apply {
            setCancelable(false)
        }
        viewModel.operation.observe(viewLifecycleOwner) {
            if (it == TaskViewModel.Operation.LOADING) {
                progressBarDialog.show()
            } else progressBarDialog.dismiss()
        }

        viewModel.done.observe(viewLifecycleOwner) {
            if (it == true) {
                requireActivity().setResult(AppCompatActivity.RESULT_OK, resultIntent)
                requireActivity().finish()
            }
        }

        return view
    }

    private fun getExistedItem(): TodoItem? {
        return if (Build.VERSION.SDK_INT >= 33) {
            requireActivity().intent.getParcelableExtra("item", TodoItem::class.java)
        } else requireActivity().intent.getParcelableExtra("item")
    }
}