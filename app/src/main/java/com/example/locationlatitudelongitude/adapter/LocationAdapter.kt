package com.example.locationlatitudelongitude.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locationlatitudelongitude.R
import com.example.locationlatitudelongitude.dbLocation

class LocationAdapter(private var datalist:List<dbLocation>): RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.location_items,parent,false)
        return ViewHolder(view)

    }
    override fun getItemCount(): Int {
       return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val locationModel=datalist[position]
      holder.lat.text=locationModel.lat
      holder.long.text=locationModel.longi
      holder.date.text=locationModel.date
    }
    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView){
    val lat:TextView=itemView.findViewById(R.id.latitude)
    val long:TextView=itemView.findViewById(R.id.longitude)
    val date:TextView=itemView.findViewById(R.id.Date)
    }
}