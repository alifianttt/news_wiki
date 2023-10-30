package com.submission.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.submission.gameapp.R
import com.submission.gameapp.databinding.ItemArticlesBinding
import com.submission.newsapp.model.Article

class ArticlesAdapter(private val listener: ArticleListener? = null): RecyclerView.Adapter<ArticlesAdapter.ArticlesHolder>() {

    private val _diffutil = object :  DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.source.id == newItem.source.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }


    private val asyncDiffer = AsyncListDiffer(this, _diffutil)

    private val data get() = asyncDiffer.currentList

    private val originalData = mutableListOf<Article>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesHolder {
        return ArticlesHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_articles, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ArticlesHolder, position: Int) {
        holder.bind(data[position], listener)
    }

    class ArticlesHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(article: Article, listener: ArticleListener?) = with(itemView){
            val binding = ItemArticlesBinding.bind(this)
            Glide.with(this).load(article.urlToImage).centerCrop().placeholder(R.drawable.alert_image).into(binding.imgNews)
            binding.titleNews.text= article.title
            setOnClickListener {
                listener?.onClick(article)
            }
        }
    }

    fun setData(list: List<Article>){
        val oldList = data.toMutableList()
        oldList.addAll(list)
        originalData.addAll(oldList)
        asyncDiffer.submitList(oldList)
    }

    fun filteredArticle(query: String){
        val filtered = if (query.isEmpty()){
            originalData.toList()
        } else {
            originalData.filter { article ->
                article.title.contains(query, ignoreCase = true)
            }
        }

        asyncDiffer.submitList(filtered)
    }

    interface ArticleListener{
        fun onClick(article: Article)
    }

}