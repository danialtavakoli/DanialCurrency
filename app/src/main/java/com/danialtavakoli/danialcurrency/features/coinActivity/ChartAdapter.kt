package com.danialtavakoli.danialcurrency.features.coinActivity

import com.danialtavakoli.danialcurrency.apiManager.model.ChartData
import com.robinhood.spark.SparkAdapter

class ChartAdapter(
    private val historicalData: List<ChartData.Data>,
    private val baseLine: String?
) : SparkAdapter() {
    override fun getCount(): Int {
        return historicalData.size
    }

    override fun getItem(index: Int): ChartData.Data {
        return historicalData[index]
    }

    override fun getY(index: Int): Float {
        return historicalData[index].close.toFloat()
    }

    override fun hasBaseLine(): Boolean {
        return true
    }

    override fun getBaseLine(): Float {
        return baseLine?.toFloat() ?: super.getBaseLine()
    }
}