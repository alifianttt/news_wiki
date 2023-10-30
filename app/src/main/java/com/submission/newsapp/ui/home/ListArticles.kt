package com.submission.newsapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.submission.gameapp.R
import com.submission.gameapp.databinding.ActivityListArticlesBinding
import com.submission.newsapp.adapter.ArticlesAdapter
import com.submission.newsapp.data.source.remote.network.Status
import com.submission.newsapp.ext.ConnectionLiveData
import com.submission.newsapp.ext.setVisible
import com.submission.newsapp.model.Article
import com.submission.newsapp.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

class ListArticles : AppCompatActivity() {

    companion object{
        const val SOURCE_ID = "source"
        fun newIntent(context: Context, sourceId: String) = Intent(context, ListArticles::class.java).apply {
            putExtra(SOURCE_ID, sourceId)
        }
    }

    private val binding: ActivityListArticlesBinding by lazy {
        ActivityListArticlesBinding.inflate(layoutInflater)
    }

    private val newsViewModel: NewsViewModel by lazy {
        ViewModelProviders.of(this)[NewsViewModel::class.java]
    }

    private val conneectionStatus: ConnectionLiveData by lazy {
        ConnectionLiveData(this)
    }
    private var idSource = ""
    private var search = ""
    private var isSearchActivce = false
    private var isEnabledScrolling = false
    private lateinit var newsAdapter: ArticlesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private val listener = object : ArticlesAdapter.ArticleListener{
        override fun onClick(article: Article) {
            startActivity(DetailNews.newIntent(this@ListArticles, article.url))
        }

    }
    private fun initView(){
        idSource = intent.getStringExtra(SOURCE_ID) ?: ""
        newsAdapter = ArticlesAdapter( listener)
        binding.swrList.isEnabled = false
        lifecycleScope.launch {
            newsViewModel.fetchArticlesWithPaging("", idSource)
        }
        conneectionStatus.observe(this){ isConnect ->
            binding.layoutNetwork.root.setVisible(!isConnect)
            if (!isConnect){
                binding.layoutNetwork.apply {
                    txtError.text = getString(R.string.error_network)
                }
            }
        }

        binding.layoutNetwork.imgClose.setOnClickListener {
            binding.layoutNetwork.root.setVisible(false)
        }
        binding.rvArticles.apply {
            layoutManager = LinearLayoutManager(this@ListArticles)
            adapter = newsAdapter
        }
        newsViewModel.getListArticle().observe(this){
            when(it.status){
                Status.LOADING -> binding.swrList.isRefreshing = true
                Status.ERROR -> {
                    binding.swrList.isRefreshing = false
                    binding.layoutNetwork.apply {
                        root.setVisible(true)
                        txtError.text = it.message
                    }
                }
                Status.SUCCES -> {
                    binding.swrList.isRefreshing = false
                    it.data?.let { data ->
                        if (data.articles.isEmpty()){
                            binding.notFound.root.visibility = View.VISIBLE
                            binding.rvArticles.visibility = View.GONE
                        } else {
                            binding.notFound.root.visibility = View.GONE
                            binding.rvArticles.visibility = View.VISIBLE
                            isEnabledScrolling = data.totalResults >= 14
                            newsAdapter.setData(data.articles)
                        }
                    }

                }
            }
        }

        binding.rvArticles.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isSearchActivce && isEnabledScrolling){
                    if (!recyclerView.canScrollVertically(1)){
                        lifecycleScope.launch {
                            newsViewModel.fetchArticlesWithPaging("", idSource)
                        }
                    }
                }
            }

        })
        binding.searchSource.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isSearchActivce = !s.isNullOrEmpty()
                search = s.toString()
                newsAdapter.filteredArticle(search)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }
}