package com.rooze.todolistwidget.models

import com.rooze.todolistwidget.entities.Todo
import com.rooze.todolistwidget.entities.TodoModel

class DummyTodoModel : TodoModel {
    override fun getAll(widgetId: Int): List<Todo> {
        return listOf(
            Todo(1, "Hello", false, 1),
            Todo(2, "Hii", false, 1),
            Todo(3, "Team meeting", false, 1),
        )
    }

    override fun getOne(todoId: Long): Todo? {
        return null
    }

    override fun save(todo: Todo) {
    }

    override fun delete(todoId: Long) {
    }
}