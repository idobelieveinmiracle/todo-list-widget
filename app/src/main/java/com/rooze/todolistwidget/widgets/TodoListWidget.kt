package com.rooze.todolistwidget.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.rooze.todolistwidget.R
import com.rooze.todolistwidget.activities.AddActivity
import com.rooze.todolistwidget.commons.Constants
import com.rooze.todolistwidget.models.TodoModelProvider
import com.rooze.todolistwidget.services.TodoListWidgetService
import kotlinx.coroutines.*

class TodoListWidget : AppWidgetProvider() {
    companion object {
        private const val TAG = "TodoListWidget"
    }

    private val coroutineScope by lazy {
        CoroutineScope(Dispatchers.Main)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action ?: return
        val widgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        Log.i(TAG, "onReceive: $action $widgetId")

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, javaClass))
                .forEach { handleAction(it, action, intent, context) }
        } else {
            handleAction(widgetId, action, intent, context)
        }
    }

    private fun handleAction(
        widgetId: Int,
        action: String,
        intent: Intent,
        context: Context
    ) {
        when (action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                initWidget(widgetId, context)
            }
            Constants.ACTION_REFRESH -> {
                AppWidgetManager.getInstance(context)
                    .notifyAppWidgetViewDataChanged(widgetId, R.id.list_todo)
            }
            Constants.ACTION_ITEM_CLICKED -> {
                val todoId = intent.getLongExtra(Constants.EXTRA_TODO_ID, 0)
                Log.i(TAG, "handleAction: $todoId")
                when (intent.getIntExtra(Constants.EXTRA_CLICK_TYPE, 0)) {
                    Constants.CHECK_CLICK -> checkTodo(widgetId, todoId, context)
                    Constants.DELETE_CLICK -> deleteTodo(widgetId, todoId, context)
                }
            }
        }
    }

    private fun checkTodo(widgetId: Int, todoId: Long, context: Context) {
        Log.i(TAG, "checkTodo: $todoId")
        val pendingResult = goAsync()
        coroutineScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.i(TAG, "checkTodo: error $throwable")
            Toast.makeText(
                context.applicationContext,
                "Check failed!",
                Toast.LENGTH_SHORT
            ).show()
        }) {
            val todoModel = TodoModelProvider.getTodoModel(context.applicationContext)
            val res = withContext(Dispatchers.IO) {
                withTimeout(5000) {
                    val todo = todoModel.getOne(todoId)
                    if (todo != null) {
                        todoModel.save(
                            todo.copy(
                                checked = !todo.checked
                            )
                        )
                        true
                    } else {
                        false
                    }
                }
            }
            if (res) {
                AppWidgetManager.getInstance(context)
                    .notifyAppWidgetViewDataChanged(widgetId, R.id.list_todo)
            }
        }.invokeOnCompletion { cause ->
            if (cause is TimeoutCancellationException) {
                Toast.makeText(
                    context.applicationContext,
                    "Check timeout!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            pendingResult.finish()
        }
    }

    private fun deleteTodo(widgetId: Int, todoId: Long, context: Context) {
        Log.i(TAG, "deleteTodo: $todoId")
        val pendingResult = goAsync()
        coroutineScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.i(TAG, "deleteTodo: error $throwable")
            Toast.makeText(
                context.applicationContext,
                "Delete failed!",
                Toast.LENGTH_SHORT
            ).show()
        }) {
            val todoModel = TodoModelProvider.getTodoModel(context.applicationContext)
            withContext(Dispatchers.IO) {
                withTimeout(5000) {
                    todoModel.delete(todoId)
                }
            }
            AppWidgetManager.getInstance(context)
                .notifyAppWidgetViewDataChanged(widgetId, R.id.list_todo)
        }.invokeOnCompletion { cause ->
            if (cause is TimeoutCancellationException) {
                Toast.makeText(
                    context.applicationContext,
                    "Delete timeout!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            pendingResult.finish()
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.i(TAG, "onUpdate: ${appWidgetIds.contentToString()}")
        for (widgetId in appWidgetIds) {
            initWidget(widgetId, context, appWidgetManager)
        }
    }

    private fun initWidget(
        widgetId: Int,
        context: Context,
        appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_todo_list)
        initViews(views, widgetId, context)
        appWidgetManager.updateAppWidget(widgetId, views)
    }

    private fun initViews(views: RemoteViews, widgetId: Int, context: Context) {
        views.setRemoteAdapter(
            R.id.list_todo,
            Intent(context, TodoListWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
        )

        views.setPendingIntentTemplate(
            R.id.list_todo,
            getItemsPendingIntent(widgetId, context)
        )
        views.setOnClickPendingIntent(
            R.id.refresh_button,
            getRefreshPendingIntent(widgetId, context)
        )
        views.setOnClickPendingIntent(
            R.id.add_button,
            getAddPendingIntent(widgetId, context)
        )
    }

    private fun getItemsPendingIntent(widgetId: Int, context: Context): PendingIntent {
        val intent = Intent(
            context,
            javaClass
        ).apply {
            action = Constants.ACTION_ITEM_CLICKED
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }

        return PendingIntent.getBroadcast(
            context,
            widgetId,
            intent,
            pendingIntentFlags
        )
    }

    private fun getRefreshPendingIntent(widgetId: Int, context: Context): PendingIntent {
        val intent = Intent(context, javaClass).apply {
            action = Constants.ACTION_REFRESH
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }

        return PendingIntent.getBroadcast(
            context,
            widgetId,
            intent,
            pendingIntentFlags
        )
    }

    private fun getAddPendingIntent(widgetId: Int, context: Context): PendingIntent {
        val intent = Intent(context, AddActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return PendingIntent.getActivity(
            context,
            widgetId,
            intent,
            pendingIntentFlags
        )
    }

    private val pendingIntentFlags by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
}