package com.rooze.todolistwidget.models.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoEntity::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        private var database: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase {
            if (database == null) {
                database = Room.databaseBuilder(
                    context,
                    TodoDatabase::class.java,
                    "todo-db"
                ).build()
            }
            return database!!
        }
    }
}