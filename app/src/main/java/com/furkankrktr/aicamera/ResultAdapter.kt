package com.furkankrktr.aicamera

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ResultAdapter(private val resultList: ArrayList<ResultModel>) :
    RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return ResultViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.itemView.nameTextView.text = resultList[position].name
        val guvenPercent = resultList[position].guven * 100
        holder.itemView.guvenTextView.text = "%${guvenPercent}"
    }

    override fun getItemCount(): Int {
        return resultList.size
    }
}