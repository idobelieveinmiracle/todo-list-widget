package com.rooze.todolistwidget.models

import android.content.Context
import com.rooze.todolistwidget.entities.TodoModel
import com.rooze.todolistwidget.models.room.RoomTodoModel

object TodoModelProvider {
    private var todoModel: TodoModel? = null

    fun getTodoModel(context: Context): TodoModel {
        if (todoModel == null) {
            todoModel = RoomTodoModel(context)
        }
        return todoModel!!
    }
}