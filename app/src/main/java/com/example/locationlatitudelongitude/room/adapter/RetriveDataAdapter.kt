package com.example.locationlatitudelongitude.room.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.locationlatitudelongitude.R

class RetriveDataAdapter(private var datalist: ArrayList<String>): RecyclerView.Adapter<RetriveDataAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.retrive_items,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        Glide.with(holder.itemView.getContext()).load(datalist.get(position)).into(holder.image);
    }

    override fun getItemCount(): Int {
        return datalist.size
    }
    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView){
        val image: ImageView =itemView.findViewById(R.id.images)

    }
}