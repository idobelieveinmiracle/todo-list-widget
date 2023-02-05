package com.rooze.todolistwidget.models.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo WHERE widget_id = :widgetId")
    fun getTodosByWidgetId(widgetId: Int): List<TodoEntity>

    @Query("SELECT *  FROM todo WHERE id = :todoId")
    fun getTodosByTodoId(todoId: Long): List<TodoEntity>

    @Insert(onConflict = REPLACE)
    fun save(todoEntity: TodoEntity)

    @Query("DELETE FROM todo WHERE id = :todoId")
    fun delete(todoId: Long)
}