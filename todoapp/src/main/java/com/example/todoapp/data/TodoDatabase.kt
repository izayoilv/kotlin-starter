package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao

  companion object {
    @Volatile
    private var instance: TodoDatabase? = null

    fun getInstance(context: Context): TodoDatabase =
      instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(
          context.applicationContext,
          TodoDatabase::class.java,
          "todo.db",
        ).fallbackToDestructiveMigration().build().also { instance = it }
      }
  }
}
