package com.rey.newskatta.di

import android.app.Application
import androidx.room.Room
import com.rey.newskatta.data.local.NewsDao
import com.rey.newskatta.data.local.NewsDatabase
import com.rey.newskatta.data.local.NewsTypeConverter
import com.rey.newskatta.data.manager.LocalUserManagerImpl
import com.rey.newskatta.data.remote.NewsApi
import com.rey.newskatta.data.repository.NewsRepositoryImpl
import com.rey.newskatta.domain.manager.LocalUserManager
import com.rey.newskatta.domain.repository.NewsRepository
import com.rey.newskatta.domain.usecases.app_entry.AppEntryUseCases
import com.rey.newskatta.domain.usecases.app_entry.ReadAppEntry
import com.rey.newskatta.domain.usecases.app_entry.SaveAppEntry
import com.rey.newskatta.domain.usecases.news.DeleteArticle
import com.rey.newskatta.domain.usecases.news.GetNews
import com.rey.newskatta.domain.usecases.news.NewsUseCases
import com.rey.newskatta.domain.usecases.news.SearchNews
import com.rey.newskatta.domain.usecases.news.SelectArticles
import com.rey.newskatta.domain.usecases.news.UpsertArticle
import com.rey.newskatta.util.Constants.BASE_URL
import com.rey.newskatta.util.Constants.NEWS_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalUserManger(
        application: Application
    ): LocalUserManager = LocalUserManagerImpl(application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localUserManager: LocalUserManager
    ) = AppEntryUseCases(
        readAppEntry = ReadAppEntry(localUserManager),
        saveAppEntry = SaveAppEntry(localUserManager)
    )




    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApi: NewsApi
    ): NewsRepository = NewsRepositoryImpl(newsApi)

    @Provides
    @Singleton
    fun provideNewsUseCases(
        newsRepository: NewsRepository,
        newsDao: NewsDao
    ): NewsUseCases{
        return NewsUseCases(
            getNews = GetNews(newsRepository),
            searchNews = SearchNews(newsRepository),
            upsertArticle = UpsertArticle(newsDao),
            deleteArticle = DeleteArticle(newsDao),
            selectArticles = SelectArticles(newsDao)
        )
    }

    @Provides
    @Singleton
    fun provideNewsDatabase(
        application: Application
    ): NewsDatabase{
        return Room.databaseBuilder(
            context = application,
            klass = NewsDatabase::class.java,
            name = NEWS_DATABASE_NAME
        ).addTypeConverter(NewsTypeConverter())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsDao(
        newsDatabase: NewsDatabase
    ): NewsDao = newsDatabase.newsDao
}