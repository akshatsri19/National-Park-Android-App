package com.example.project_g08.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.Navigation
import com.example.project_g08.R
import com.example.project_g08.databinding.ListItemParkBinding
import com.example.project_g08.model.ParkDataItemFromDb

class ParkAdapter(context: Context, parksList: List<ParkDataItemFromDb>) :
    ArrayAdapter<ParkDataItemFromDb>(context, 0, parksList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var binding = ListItemParkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val park = getItem(position)
        park?.let {
            binding.tvParkName.text = it.parkName
            binding.tvParkAddress.text = it.address
        }
        return binding.root
    }
}