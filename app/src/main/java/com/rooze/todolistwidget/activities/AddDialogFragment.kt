package com.rooze.todolistwidget.activities

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rooze.todolistwidget.R
import com.rooze.todolistwidget.models.TodoModelProvider

class AddDialogFragment : DialogFragment() {

    private lateinit var todoText: EditText
    private lateinit var saveButton: View

    private lateinit var viewModel: AddViewModel

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetId = activity?.intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoText = view.findViewById(R.id.dialog_text)
        saveButton = view.findViewById(R.id.save_button)

        todoText.addTextChangedListener { text ->
            viewModel.setText(text.toString())
        }
        saveButton.setOnClickListener {
            viewModel.save(widgetId)
        }

        initViewModel()
    }

    private fun initViewModel() {
        activity?.let { activity ->
            viewModel = ViewModelProvider(
                activity,
                AddViewModel.Factory(
                    TodoModelProvider.getTodoModel(activity.applicationContext)
                )
            )[AddViewModel::class.java]
            viewModel.error.observe(activity) { message ->
                if (message.isNotEmpty()) {
                    Toast.makeText(
                        activity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.resetError()
                }
            }
            viewModel.text.observe(activity) { text ->
                if (text != todoText.text.toString()) {
                    todoText.setText(text)
                }
            }
            viewModel.saved.observe(activity) { saved ->
                if (saved) {
                    Toast.makeText(
                        activity.applicationContext,
                        "Saved",
                        Toast.LENGTH_SHORT
                    ).show()
                    notifyListWidget(activity.applicationContext)
                    dismiss()
                }
            }
        }
    }

    private fun notifyListWidget(context: Context) {
        AppWidgetManager.getInstance(context)
            .notifyAppWidgetViewDataChanged(widgetId, R.id.list_todo)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity?.finish()
    }
}