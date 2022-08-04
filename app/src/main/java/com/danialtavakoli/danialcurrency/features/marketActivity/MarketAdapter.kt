package com.danialtavakoli.danialcurrency.features.marketActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danialtavakoli.danialcurrency.R
import com.danialtavakoli.danialcurrency.apiManager.BASE_URL_IMAGE
import com.danialtavakoli.danialcurrency.apiManager.model.CoinsData
import com.danialtavakoli.danialcurrency.databinding.ItemRecyclerMarketBinding

class MarketAdapter(
    private val data: ArrayList<CoinsData.Data>,
    private val recyclerCallBack: RecyclerCallBack
) :
    RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {

    private lateinit var binding: ItemRecyclerMarketBinding

    inner class MarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bindViews(data: CoinsData.Data) {
            with(binding) {
                txtCoinName.text = data.coinInfo.fullName
                txtPrice.text = data.dISPLAY.uSD.pRICE
                txtMarketCap.text = data.dISPLAY.uSD.mKTCAP
                txtChange.text = data.dISPLAY.uSD.cHANGEPCT24HOUR + "%"
                val changePriceRaw = data.rAW.uSD.cHANGEPCT24HOUR
                if (changePriceRaw > 0)
                    txtChange.setTextColor(
                        ContextCompat.getColor(root.context, R.color.colorGain)
                    ) else if (changePriceRaw < 0)
                    txtChange.setTextColor(
                        ContextCompat.getColor(root.context, R.color.colorLoss)
                    )
                Glide.with(itemView).load(BASE_URL_IMAGE + data.coinInfo.imageUrl).into(imgItem)
            }
            itemView.setOnClickListener { recyclerCallBack.onCoinItemClicked(data) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRecyclerMarketBinding.inflate(inflater, parent, false)
        return MarketViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindViews(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface RecyclerCallBack {
        fun onCoinItemClicked(data: CoinsData.Data)
    }
}