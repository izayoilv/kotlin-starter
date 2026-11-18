package com.example.musicapp.di

import android.app.Application
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import com.example.musicapp.data.DownloadNotifications
import com.example.musicapp.data.MusicDownloadRepository
import com.example.musicapp.data.MusicDownloader
import com.example.musicapp.data.WorkManagerMusicDownloadRepository
import com.example.musicapp.worker.MetroWorkerFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface AppGraph : ViewModelGraph {
  val workerFactory: MetroWorkerFactory

  @Multibinds
  val workerProviders:
    Map<KClass<out ListenableWorker>, () -> MetroWorkerFactory.WorkerInstanceFactory<*>>

  @Provides
  fun provideContext(application: Application): Context = application

  @Provides
  @SingleIn(AppScope::class)
  fun provideWorkManager(context: Context): WorkManager = WorkManager.getInstance(context)

  @Provides
  @SingleIn(AppScope::class)
  fun provideHttpClient(): HttpClient =
    HttpClient(OkHttp) {
      expectSuccess = false
      install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 60_000
      }
    }

  @Provides
  fun provideDownloader(
    context: Context,
    client: HttpClient,
  ): MusicDownloader = MusicDownloader(context, client)

  @Provides
  fun provideNotifications(context: Context): DownloadNotifications = DownloadNotifications(context)

  @Provides
  @SingleIn(AppScope::class)
  fun provideRepository(workManager: WorkManager): MusicDownloadRepository =
    WorkManagerMusicDownloadRepository(workManager)

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(
      @Provides application: Application,
    ): AppGraph
  }
}
