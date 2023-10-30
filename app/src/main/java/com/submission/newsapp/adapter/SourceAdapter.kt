package com.submission.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.submission.gameapp.R
import com.submission.gameapp.databinding.ItemSourceBinding
import com.submission.newsapp.model.Source

class SourceAdapter( val listener: SourceListener? = null): RecyclerView.Adapter<SourceAdapter.SourceHolder>() {

    private val _diffutil = object :  DiffUtil.ItemCallback<Source>() {
        override fun areItemsTheSame(oldItem: Source, newItem: Source): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Source, newItem: Source): Boolean {
            return oldItem == newItem
        }

    }


    private val asyncDiffer = AsyncListDiffer(this, _diffutil)

    private val data get() = asyncDiffer.currentList

    private val originalData = mutableListOf<Source>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceHolder {
        return SourceHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_source, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SourceHolder, position: Int) {
        holder.bind(data[position], listener)
    }

    class SourceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(source: Source, listener: SourceListener?) = with(itemView){
            val binding = ItemSourceBinding.bind(this)
            binding.sourceName.text = source.name

            setOnClickListener {
                listener?.onClick(source)
            }
        }
    }


    fun setData(newData: List<Source>){
        val oldItem = data.toMutableList()
        oldItem.addAll(newData)
        originalData.addAll(oldItem)
        asyncDiffer.submitList(oldItem)
    }


    fun filteredData(query: String){
        val filteredData = if (query.isEmpty()) {
            originalData.toList()
        } else {
            originalData.filter { source ->
                source.name.contains(query, ignoreCase = true)
            }
        }
        asyncDiffer.submitList(filteredData)
    }


    interface SourceListener{
        fun onClick(source: Source)
    }
}