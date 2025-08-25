package com.sudokumaster.android.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sudokumaster.android.data.local.AuthTokenStorage
import com.sudokumaster.android.data.local.SudokuDatabase
import com.sudokumaster.android.data.remote.ApiService
import com.sudokumaster.android.data.repository.AuthRepositoryImpl
import com.sudokumaster.android.data.repository.SudokuRepositoryImpl
import com.sudokumaster.android.domain.repository.AuthRepository
import com.sudokumaster.android.domain.repository.SudokuRepository
import com.sudokumaster.android.utils.AdManagerStub
import com.sudokumaster.android.utils.NetworkMonitor
import com.sudokumaster.android.utils.PerformanceMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    @Provides
    @Singleton
    fun provideAuthTokenStorage(@ApplicationContext context: Context): AuthTokenStorage {
        return AuthTokenStorage(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(authTokenStorage: AuthTokenStorage): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            
            // Skip auth for certain endpoints
            val skipAuth = originalRequest.url.encodedPath.let { path ->
                path.contains("/login") || 
                path.contains("/register") || 
                path.contains("/refresh") ||
                path == "/api" // Health check
            }
            
            if (skipAuth) {
                chain.proceed(originalRequest)
            } else {
                try {
                    val tokens = kotlinx.coroutines.runBlocking {
                        authTokenStorage.getAuthTokens()
                    }
                    
                    if (tokens != null && !tokens.isExpired) {
                        val authenticatedRequest = originalRequest.newBuilder()
                            .addHeader("Authorization", "Bearer ${tokens.accessToken}")
                            .build()
                        chain.proceed(authenticatedRequest)
                    } else {
                        chain.proceed(originalRequest)
                    }
                } catch (e: Exception) {
                    chain.proceed(originalRequest)
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://sudoku-master-api-93673815784.us-central1.run.app/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSudokuDatabase(@ApplicationContext context: Context): SudokuDatabase {
        return Room.databaseBuilder(
            context,
            SudokuDatabase::class.java,
            "sudoku_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun providePerformanceMonitor(@ApplicationContext context: Context): PerformanceMonitor {
        return PerformanceMonitor(context)
    }

    @Provides
    @Singleton
    fun provideAdManager(
        @ApplicationContext context: Context,
        performanceMonitor: PerformanceMonitor
    ): AdManagerStub {
        return AdManagerStub(context, performanceMonitor)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        authTokenStorage: AuthTokenStorage
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, authTokenStorage)
    }

    @Provides
    @Singleton
    fun provideSudokuRepository(
        apiService: ApiService,
        database: SudokuDatabase,
        authRepository: AuthRepository
    ): SudokuRepository {
        return SudokuRepositoryImpl(apiService, database, authRepository)
    }
}