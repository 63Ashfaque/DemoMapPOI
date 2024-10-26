package com.ashfaque.demopoi.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ashfaque.demopoi.R
import com.ashfaque.demopoi.databinding.ItemCardBinding
import com.ashfaque.demopoi.roomdb.EntityDataClass


class MyAdapter(private var items: List<EntityDataClass>, private val clickListener: MyClickListener) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val mBinding = ItemCardBinding.inflate(inflater, parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item,clickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(private val itemBinding: ItemCardBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: EntityDataClass, clickListener: MyClickListener) {
            itemBinding.tvTitle.text = "Title: ${item.title.uppercase()}"
            itemBinding.tvOwnerName.text = "Owner Name: ${item.ownerName}"
            itemBinding.tvLocationName.text = "Location Name: ${item.locationName}"
            itemBinding.tvEstablishDate.text = "Established Date: ${item.establishedDate}"
            itemBinding.tvlatLong.text = "Lat Lng: ${item.lat}, ${item.lng}"
            itemBinding.tvTagName.text = item.tag
            itemBinding.tvDateTime.text = item.createdDate

            itemBinding.imageViewMenu.setOnClickListener {
                showCustomPopup(it,item,clickListener)
            }
        }

        private fun showCustomPopup(anchor: View,item:EntityDataClass, clickListener: MyClickListener) {
            val dialog = Dialog(anchor.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_custom_popup)

            // Set up click listeners
            val updateOption = dialog.findViewById<TextView>(R.id.tvUpdate)
            val deleteOption = dialog.findViewById<TextView>(R.id.tvDelete)
            val geoJsonOption = dialog.findViewById<TextView>(R.id.tvGeoJson)

            updateOption.setOnClickListener {
                clickListener.onUpdateItemClick(item)
                dialog.dismiss()
            }

            deleteOption.setOnClickListener {
                clickListener.onDeleteItemClick(item)
                dialog.dismiss()
            }

            geoJsonOption.setOnClickListener {
                clickListener.onGeoJsonItemClick(item)
                dialog.dismiss()
            }

            dialog.window?.apply {
                val location = IntArray(2)
                anchor.getLocationOnScreen(location)
               setGravity(Gravity.TOP or Gravity.START)
                attributes = attributes.apply {
                    x = location[0]
                    y = location[1] - anchor.height
                }
                setBackgroundDrawableResource(android.R.color.transparent)
            }
            dialog.show()
        }


    }

    interface MyClickListener {
        fun onUpdateItemClick(item: EntityDataClass)
        fun onDeleteItemClick(item: EntityDataClass)
        fun onGeoJsonItemClick(item: EntityDataClass)
    }


}