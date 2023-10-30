package com.submission.newsapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.gameapp.R
import com.submission.newsapp.adapter.SourceAdapter
import com.submission.newsapp.data.source.remote.network.Status
import com.submission.gameapp.databinding.ActivityNewsBinding
import com.submission.newsapp.ext.ConnectionLiveData
import com.submission.newsapp.ext.setVisible
import com.submission.newsapp.model.Source
import com.submission.newsapp.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

class SourceActivity : AppCompatActivity() {
    private val binding: ActivityNewsBinding by lazy {
        ActivityNewsBinding.inflate(layoutInflater)
    }

    private val newsViewModel : NewsViewModel by lazy {
        ViewModelProviders.of(this)[NewsViewModel::class.java]
    }

    private val conneectionStatus: ConnectionLiveData by lazy {
        ConnectionLiveData(this)
    }
    companion object{
        const val CATEGORY_KEY = "category"
        fun newIntent(context: Context, category: String) = Intent(context, SourceActivity::class.java).apply {
            putExtra(CATEGORY_KEY, category)
        }
    }
    private var category = ""
    private lateinit var sourceAdapter: SourceAdapter
    private var isEmpty = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private val listener = object : SourceAdapter.SourceListener{
        override fun onClick(source: Source) {
            startActivity(ListArticles.newIntent(this@SourceActivity, source.id))
        }

    }
    private fun initView(){
        category = intent.getStringExtra(CATEGORY_KEY) ?: ""
        sourceAdapter = SourceAdapter( listener)
        binding.swrList.isEnabled = false
        conneectionStatus.observe(this){ isConnect ->
            binding.layoutNetwork.root.setVisible(!isConnect)
            if (!isConnect){
                binding.layoutNetwork.apply {
                    txtError.text = getString(R.string.error_network)
                }
            }
            if (isConnect && isEmpty){
                lifecycleScope.launch {
                    newsViewModel.fetchListSource(category)
                }
            }
        }

        binding.layoutNetwork.imgClose.setOnClickListener {
            binding.layoutNetwork.root.setVisible(false)
        }

        newsViewModel.getListSource().observe(this){
            when(it.status){
                Status.LOADING -> binding.swrList.isRefreshing =  true
                Status.ERROR -> {
                    binding.swrList.isRefreshing = false
                    binding.layoutNetwork.apply {
                        root.setVisible(true)
                        txtError.text = it.message
                    }
                }
                Status.SUCCES -> {
                    it.data?.let { base ->
                        isEmpty = base.sources.isEmpty()
                        if (base.sources.isEmpty()){
                            binding.notFound.root.visibility = View.VISIBLE
                            binding.rvNews.visibility = View.GONE
                        } else {
                            val list = mutableListOf<Source>()
                            binding.notFound.root.visibility = View.GONE
                            binding.rvNews.visibility = View.VISIBLE
                            list.clear()
                            list.addAll(base.sources)
                            sourceAdapter.setData(list)
                        }
                    }
                    binding.swrList.isRefreshing = false
                    binding.rvNews.apply {
                        layoutManager = LinearLayoutManager(this@SourceActivity)
                        adapter = sourceAdapter
                    }
                }
            }
        }

        binding.searchSource.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                sourceAdapter.filteredData(s.toString())
            }

        })
    }
}