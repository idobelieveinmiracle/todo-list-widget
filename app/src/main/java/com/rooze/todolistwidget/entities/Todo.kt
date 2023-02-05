package com.rooze.todolistwidget.entities

data class Todo(
    val todoId: Long,
    val title: String,
    val checked: Boolean,
    val widgetId: Int
)
