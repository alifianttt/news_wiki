package com.submission.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.submission.gameapp.R
import com.submission.gameapp.databinding.ItemCategoryBinding
import com.submission.newsapp.ext.capitalizeFirstLetter
import com.submission.newsapp.model.Source

class NewsAdapter(private var data: ArrayList<Source> = arrayListOf(), val listener: NewsListener? = null) : RecyclerView.Adapter<NewsAdapter.NewsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {
        return NewsHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    override fun onBindViewHolder(holder: NewsHolder, position: Int) {
        holder.bind(data[position], listener)
    }

    override fun getItemCount(): Int = data.size

    class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(news: Source, listener: NewsListener?) = with(itemView){
            val binding = ItemCategoryBinding.bind(itemView)
            binding.category.text = capitalizeFirstLetter(news.category)

            binding.imgNews.setImageResource(
                when(news.category){
                    "general" -> R.drawable.general
                    "business" -> R.drawable.bussines
                    "technology" -> R.drawable.tech
                    "sports" -> R.drawable.soccer
                    "entertainment" -> R.drawable.entertainment
                    "health" -> R.drawable.health
                    "science" -> R.drawable.science
                    else -> 0
                }
            )
            setOnClickListener{
                listener?.onClick(news)
            }
        }
    }

    interface NewsListener{
        fun onClick(source: Source)
    }
}