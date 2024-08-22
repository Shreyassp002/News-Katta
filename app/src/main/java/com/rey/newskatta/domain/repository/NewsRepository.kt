package com.rey.newskatta.domain.repository

import androidx.paging.PagingData
import com.rey.newskatta.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(sources: List<String>): Flow<PagingData<Article>>
}