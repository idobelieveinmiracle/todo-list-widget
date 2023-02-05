package com.rooze.todolistwidget.models.room

import android.content.Context
import com.rooze.todolistwidget.entities.Todo
import com.rooze.todolistwidget.entities.TodoModel

class RoomTodoModel(context: Context) : TodoModel {
    private val todoDao = TodoDatabase.getDatabase(context).todoDao()

    override fun getAll(widgetId: Int): List<Todo> {
        return todoDao.getTodosByWidgetId(widgetId).map {
            Todo(it.id, it.title, it.checked, it.widgetId)
        }
    }

    override fun getOne(todoId: Long): Todo? {
        val todos = todoDao.getTodosByTodoId(todoId)
        if (todos.isEmpty()) {
            return null
        }
        return todos.first().let {
            Todo(it.id, it.title, it.checked, it.widgetId)
        }
    }

    override fun save(todo: Todo) {
        todoDao.save(todo.let {
            TodoEntity(it.todoId, it.title, it.checked, it.widgetId)
        })
    }

    override fun delete(todoId: Long) {
        todoDao.delete(todoId)
    }
}