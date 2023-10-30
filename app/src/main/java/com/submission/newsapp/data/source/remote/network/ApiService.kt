package com.submission.newsapp.data.source.remote.network

import com.submission.newsapp.model.BaseArticleResponse
import com.submission.newsapp.model.BaseSource
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("top-headlines/sources")
    fun showNewsSource(@Query("apiKey") apiKey: String) : Call<BaseSource>

    @GET("top-headlines/sources")
    fun showSourceByCategory(@Query("apiKey") apiKey: String, @Query("language") language: String = "en", @Query("category") category: String, @Query("q") sources: String = "") : Call<BaseSource>

    @GET("everything")
    fun showListArticles(@Query("q") query: String, @Query("sources") sourceId: String, @Query("apiKey") apiKey: String) : Call<BaseArticleResponse>

    @GET("everything")
    fun showArticleWithPaging(@Query("q") query: String, @Query("sources") sourceId: String, @Query("apiKey") apiKey: String, @Query("page") page: Int, @Query("pageSize") pageSize: Int) : Call<BaseArticleResponse>
}