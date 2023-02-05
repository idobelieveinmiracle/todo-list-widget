package com.rooze.todolistwidget.entities

interface TodoModel {
    fun getAll(widgetId: Int): List<Todo>
    fun getOne(todoId: Long): Todo?
    fun save(todo: Todo)
    fun delete(todoId: Long)
}