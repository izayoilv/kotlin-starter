package com.example.notesapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [NoteEntity::class],
  version = 1,
  exportSchema = true,
)
abstract class NotesDatabase : RoomDatabase() {
  abstract fun noteDao(): NoteDao

  companion object {
    fun build(context: Context): NotesDatabase =
      Room
        .databaseBuilder(context.applicationContext, NotesDatabase::class.java, "notes.db")
        .build()
  }
}
