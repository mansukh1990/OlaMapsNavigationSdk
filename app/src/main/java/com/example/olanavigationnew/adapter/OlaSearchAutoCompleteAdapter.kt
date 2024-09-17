package com.example.olanavigationnew.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.olanavigationnew.databinding.ItemSearchLocationBinding
import com.example.olanavigationnew.model.Prediction

class OlaSearchAutoCompleteAdapter(listener: OnAddressClickListener) :
    ListAdapter<Prediction, OlaSearchAutoCompleteAdapter.OlaSearchAutoCompleteViewHolder>(
        DiffCallback()
    ) {

    private var mListener = listener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OlaSearchAutoCompleteViewHolder {
        val binding =
            ItemSearchLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OlaSearchAutoCompleteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OlaSearchAutoCompleteViewHolder, position: Int) {
        holder.bind(getItem(position), mListener, position)
    }

    class OlaSearchAutoCompleteViewHolder(binding: ItemSearchLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val mBinding = binding
        fun bind(item: Prediction?, mListener: OnAddressClickListener, position: Int) {
            mBinding.apply {
                tvAddress.text = item?.description.toString()
                clAddress.setOnClickListener {
                    mListener.onAddressSelected(item, position, mListener)
                }
            }
        }

        private fun formatDistance(distanceInMeters: Int): String {
            return if (distanceInMeters < 1000) {
                "$distanceInMeters m"
            } else {
                val distanceInKilometers = distanceInMeters / 1000.0
                "${"%.2f".format(distanceInKilometers)} km"
            }
        }
    }


    private class DiffCallback : DiffUtil.ItemCallback<Prediction>() {
        override fun areItemsTheSame(oldItem: Prediction, newItem: Prediction): Boolean {
            return oldItem.placeId == newItem.placeId // Use a unique ID to compare items
        }

        override fun areContentsTheSame(oldItem: Prediction, newItem: Prediction): Boolean {
            return oldItem == newItem // Use the `Prediction` equals method to compare content
        }
    }

    interface OnAddressClickListener {
        fun onAddressSelected(item: Prediction?, position: Int, mListener: OnAddressClickListener)
    }
}