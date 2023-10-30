package com.submission.newsapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.submission.gameapp.BuildConfig
import com.submission.newsapp.data.source.remote.network.ApiResponse
import com.submission.newsapp.data.source.remote.network.RetrofitBuilder
import com.submission.newsapp.model.BaseArticleResponse
import com.submission.newsapp.model.BaseSource
import com.submission.newsapp.model.ErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class NewsViewModel: ViewModel() {

    private val listCategory = MutableLiveData<ApiResponse<BaseSource>>()
    private val sourceList = MutableLiveData<ApiResponse<BaseSource>>()
    private val listArticles = MutableLiveData<ApiResponse<BaseArticleResponse>>()

    private val network: RetrofitBuilder = RetrofitBuilder()

    private var page = 1
    suspend fun fetchNewsSource(){
        listCategory.postValue(ApiResponse.onLoading(null))

        try {
            val response = withContext(Dispatchers.IO){
                network.api().showNewsSource(BuildConfig.API_KEY).execute()
            }

            if (response.isSuccessful){
                listCategory.postValue(ApiResponse.onSucces(response.body()))
            } else {
                val responsError = response.errorBody()?.string()
                try {
                    val errorResponse = Gson().fromJson(responsError, ErrorResponse::class.java)
                    listCategory.postValue(ApiResponse.onError(errorResponse.message, null))
                } catch (e: Exception){
                    listCategory.postValue(ApiResponse.onError(e.message, null))
                }
            }
        } catch (e: HttpException){
            listCategory.postValue(ApiResponse.onError(e.message(), null))
        } catch (e: IOException){
            listCategory.postValue(ApiResponse.onError(e.message, null))
        } catch (e: Exception){
            listCategory.postValue(ApiResponse.onError(e.message, null))
        }
    }

    suspend fun fetchListSource(category: String){
        sourceList.postValue(ApiResponse.onLoading(null))
        try {
            val response = withContext(Dispatchers.IO){
                network.api().showSourceByCategory(BuildConfig.API_KEY, category = category).execute()
            }
            if (response.isSuccessful){
                sourceList.postValue(ApiResponse.onSucces(response.body()))
            } else {
                val responsError = response.errorBody()?.string()
                try {
                    val errorResponse = Gson().fromJson(responsError, ErrorResponse::class.java)
                    sourceList.postValue(ApiResponse.onError(errorResponse.message, null))
                } catch (e: Exception){
                    sourceList.postValue(ApiResponse.onError(e.message, null))
                }
            }
        } catch (e: HttpException){
            sourceList.postValue(ApiResponse.onError(e.message, null))
        } catch (e: IOException){
            sourceList.postValue(ApiResponse.onError(e.message, null))
        } catch (e: Exception){
            sourceList.postValue(ApiResponse.onError(e.message, null))
        }

    }

    suspend fun fetchArticlesWithPaging(q: String, source: String){
        listArticles.postValue(ApiResponse.onLoading(null))
        try {
            val response = withContext(Dispatchers.IO){
                network.api().showArticleWithPaging(q, source, BuildConfig.API_KEY, page, 15).execute()
            }

            if (response.isSuccessful){
                val baseResponse = response.body()
                Log.d("not 200 response", "respons: ${response.body()}")
                listArticles.postValue(ApiResponse.onSucces(baseResponse))
                page++
            } else {
                val responsError = response.errorBody()?.string()
                try {
                    val errorResponse = Gson().fromJson(responsError, ErrorResponse::class.java)
                    listArticles.postValue(ApiResponse.onError(errorResponse.message, null))
                    Log.d("errpr res", "error data ${errorResponse.message.substringBefore(".")}")
                } catch (e: Exception){
                    Log.d("errpr", "Exception ${e.message}")
                }
            }
        }  catch (e: HttpException){
            listArticles.postValue(ApiResponse.onError(e.message(), null))
        } catch (e: IOException){
            listArticles.postValue(ApiResponse.onError(e.message, null))
        } catch (e: Exception){
            listArticles.postValue(ApiResponse.onError(e.message, null))
        }
    }

    fun getNewsSource() : LiveData<ApiResponse<BaseSource>> = listCategory

    fun getListSource() : LiveData<ApiResponse<BaseSource>> = sourceList

    fun getListArticle() : LiveData<ApiResponse<BaseArticleResponse>> = listArticles
}