package com.submission.newsapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import com.submission.gameapp.R
import com.submission.gameapp.databinding.ActivityDetailNewsBinding
import com.submission.newsapp.ext.ConnectionLiveData
import com.submission.newsapp.ext.setVisible

class DetailNews : AppCompatActivity() {
    private val binding: ActivityDetailNewsBinding by lazy {
        ActivityDetailNewsBinding.inflate(layoutInflater)
    }

    private val conneectionStatus: ConnectionLiveData by lazy {
        ConnectionLiveData(this)
    }
    companion object{
        const val URL_KEY = "url"
        fun newIntent(context: Context, url: String) = Intent(context, DetailNews::class.java).apply {
            putExtra(URL_KEY, url)
        }
    }
    private var urlNews = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView(){
        urlNews = intent.getStringExtra(URL_KEY) ?: ""
        conneectionStatus.observe(this){ isConnect ->
            binding.layoutNetwork.root.setVisible(!isConnect)
            if (!isConnect){
                binding.layoutNetwork.apply {
                    txtError.text = getString(R.string.error_network)
                }
            }
        }
        binding.webArticle.settings.javaScriptEnabled = true
        binding.webArticle.webChromeClient = WebChromeClient()
        binding.webArticle.loadUrl(urlNews)
    }
}