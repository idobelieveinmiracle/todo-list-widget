package com.rooze.todolistwidget.models.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val checked: Boolean,
    @ColumnInfo(name = "widget_id")
    val widgetId: Int
)