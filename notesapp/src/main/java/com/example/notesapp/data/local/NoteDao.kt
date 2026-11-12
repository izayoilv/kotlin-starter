package com.example.notesapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NoteDao {
  @Query("SELECT * FROM notes ORDER BY createdAt DESC")
  abstract fun observeAll(): Flow<List<NoteEntity>>

  @Query("SELECT * FROM notes WHERE id = :id")
  abstract fun observeById(id: Long): Flow<NoteEntity?>

  @Upsert
  abstract suspend fun upsertAll(notes: List<NoteEntity>)

  @Upsert
  abstract suspend fun upsert(note: NoteEntity)

  @Query("DELETE FROM notes WHERE id = :id")
  abstract suspend fun deleteById(id: Long)

  @Query("DELETE FROM notes")
  abstract suspend fun deleteAll()

  @Query("DELETE FROM notes WHERE id NOT IN (:ids)")
  abstract suspend fun deleteMissing(ids: List<Long>)

  @Transaction
  open suspend fun sync(notes: List<NoteEntity>) {
    if (notes.isEmpty()) {
      deleteAll()
    } else {
      upsertAll(notes)
      deleteMissing(notes.map { it.id })
    }
  }
}
