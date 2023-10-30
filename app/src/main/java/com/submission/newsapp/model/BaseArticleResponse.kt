package com.submission.newsapp.model


data class BaseArticleResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)