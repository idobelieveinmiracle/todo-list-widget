package com.rooze.todolistwidget.services

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.rooze.todolistwidget.R
import com.rooze.todolistwidget.commons.Constants
import com.rooze.todolistwidget.entities.Todo
import com.rooze.todolistwidget.entities.TodoModel
import com.rooze.todolistwidget.models.TodoModelProvider

class TodoListWidgetService : RemoteViewsService() {
    companion object {
        private const val TAG = "TodoListWidgetService"
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TodoRemoteViewsFactory(
            applicationContext,
            TodoModelProvider.getTodoModel(applicationContext),
            intent
        )
    }

    private inner class TodoRemoteViewsFactory(
        private val context: Context,
        private val todoModel: TodoModel,
        intent: Intent,
    ) : RemoteViewsFactory {

        private val widgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        private val todos = mutableListOf<Todo>()

        override fun onCreate() {
        }

        override fun onDataSetChanged() {
            todos.clear()
            todos.addAll(todoModel.getAll(widgetId))
            Log.i(TAG, "onDataSetChanged: $todos")
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int {
            return todos.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_item_todo)
            updateViewItem(todos[position], views)
            return views
        }

        private fun updateViewItem(todo: Todo, views: RemoteViews) {
            views.setTextViewText(R.id.item_title, todo.title)

            if (todo.checked) {
                views.setImageViewResource(R.id.item_check, R.drawable.ic_checked)
                views.setInt(
                    R.id.item_title,
                    "setPaintFlags",
                    Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                )
            } else {
                views.setInt(
                    R.id.item_title,
                    "setPaintFlags",
                    Paint.LINEAR_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                )
                views.setImageViewResource(R.id.item_check, R.drawable.ic_unchecked)
            }

            views.setOnClickFillInIntent(R.id.item_check, Intent().apply {
                putExtra(Constants.EXTRA_TODO_ID, todo.todoId)
                putExtra(Constants.EXTRA_CLICK_TYPE, Constants.CHECK_CLICK)
            })
            views.setOnClickFillInIntent(R.id.item_delete, Intent().apply {
                putExtra(Constants.EXTRA_TODO_ID, todo.todoId)
                putExtra(Constants.EXTRA_CLICK_TYPE, Constants.DELETE_CLICK)
            })
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(context.packageName, R.layout.widget_item_todo)
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return todos[position].todoId
        }

        override fun hasStableIds(): Boolean {
            return true
        }

    }
}