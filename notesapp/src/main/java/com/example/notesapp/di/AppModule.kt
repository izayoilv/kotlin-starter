package com.example.notesapp.di

import android.content.Context
import com.example.notesapp.BuildConfig
import com.example.notesapp.data.NotesRepositoryImpl
import com.example.notesapp.data.local.NoteDao
import com.example.notesapp.data.local.NotesDatabase
import com.example.notesapp.data.remote.KtorNotesApi
import com.example.notesapp.data.remote.NotesApi
import com.example.notesapp.data.remote.NotesHttpClient
import com.example.notesapp.model.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides
  @Singleton
  fun provideDatabase(
    @ApplicationContext context: Context,
  ): NotesDatabase = NotesDatabase.build(context)

  @Provides
  @Singleton
  fun provideNoteDao(database: NotesDatabase): NoteDao = database.noteDao()

  @Provides
  @Singleton
  fun provideHttpClient(): HttpClient = NotesHttpClient.create()

  @Provides
  @Singleton
  fun provideNotesApi(client: HttpClient): NotesApi =
    KtorNotesApi(client, BuildConfig.API_BASE_URL)

  @Provides
  @Singleton
  fun provideRepository(
    api: NotesApi,
    dao: NoteDao,
  ): NotesRepository = NotesRepositoryImpl(api, dao)
}
