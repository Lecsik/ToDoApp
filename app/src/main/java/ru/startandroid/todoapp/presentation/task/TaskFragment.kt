package ru.startandroid.todoapp.presentation.task

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.get
import androidx.navigation.findNavController
import org.joda.time.LocalDate
import ru.startandroid.todoapp.R
import ru.startandroid.todoapp.databinding.FragmentTaskBinding
import ru.startandroid.todoapp.models.TodoItem
import java.util.Calendar

class TaskFragment : Fragment(R.layout.fragment_task) {

    companion object {
        const val RESULT_KEY = "TaskFragmentResultKey"
        const val RESULT_NEW_ITEM_KEY = "newItem"
        const val RESULT_DELETE_KEY = "deleteItem"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val resultBundle = bundleOf()
        val viewModel = ViewModelProvider(this).get<TaskViewModel>()

        //getExistedItem
        TaskFragmentArgs.fromBundle(requireArguments()).todoItem
            ?.let { viewModel.setExistingItem(it) }

        val binding: FragmentTaskBinding = FragmentTaskBinding.inflate(layoutInflater)

        val priorityListItems = resources.getStringArray(R.array.priorityListItems)

        //toolbar
        binding.taskScreenToolbar.apply {
            setNavigationIcon(R.drawable.property_1_close)
            inflateMenu(R.menu.top_app_task_screen_bar)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.save_text_button -> {
                        viewModel.description.value?.takeIf { it.isNotBlank() }?.let {
                            resultBundle.putParcelable(RESULT_NEW_ITEM_KEY, viewModel.save())
                        }
                        true
                    }

                    else -> super.onOptionsItemSelected(menuItem)
                }
            }
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        //description
        binding.taskDescription.apply {
            this.addTextChangedListener { text ->
                viewModel.description.value = text.let { it!!.toString() }
            }

            viewModel.description.observe(viewLifecycleOwner) {
                if (it == text?.toString()) return@observe
                setText(it)
            }
        }

        //priority
        binding.priority.apply {
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
        binding.switchDate.apply {
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
            viewModel.dueDate.observe(viewLifecycleOwner) { incomDueDate ->
                setOnCheckedChangeListener(null)
                if (incomDueDate != null) {
                    binding.dateText.text = incomDueDate.toString("dd MMMM yyyy")
                    isChecked = true
                } else {
                    binding.dateText.text = ""
                    isChecked = false
                }
                setOnCheckedChangeListener(onCheckedChangeListener)
            }
        }

        // delete button
        binding.deleteTextView.apply {
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
                resultBundle.putString(RESULT_DELETE_KEY, viewModel.remove())
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
                parentFragmentManager.setFragmentResult(RESULT_KEY, resultBundle)
                requireView().findNavController().navigateUp()
            }
        }

        return binding.root
    }
}