package com.danialtavakoli.danialcurrency.apiManager

import com.danialtavakoli.danialcurrency.apiManager.model.ChartData
import com.danialtavakoli.danialcurrency.apiManager.model.CoinsData
import com.danialtavakoli.danialcurrency.apiManager.model.NewsData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {
    private val apiService: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        apiService = retrofit.create(ApiService::class.java)
    }

    fun getNews(apiCallback: ApiCallback<ArrayList<Pair<String, String>>>) {
        apiService.getTopNews().enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
                val data = response.body()!!
                val dataToSend = arrayListOf<Pair<String, String>>()
                data.data.forEach { dataToSend.add(Pair(it.title, it.url)) }
                apiCallback.onSuccess(dataToSend)
            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }
        })
    }

    fun getCoinsList(apiCallback: ApiCallback<List<CoinsData.Data>>) {
        apiService.getTopCoins().enqueue(object : Callback<CoinsData> {
            override fun onResponse(call: Call<CoinsData>, response: Response<CoinsData>) {
                val data = response.body()!!
                apiCallback.onSuccess(data.data)
            }

            override fun onFailure(call: Call<CoinsData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }
        })
    }

    fun getChartData(
        symbol: String,
        period: String,
        apiCallback: ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>>
    ) {
        var historyPeriod = ""
        var limit = 30
        var aggregate = 1
        when (period) {
            HOUR -> {
                historyPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12
            }
            HOURS24 -> {
                historyPeriod = HISTO_HOUR
                limit = 24
            }
            WEEK -> {
                historyPeriod = HISTO_HOUR
                limit = 6
            }
            MONTH -> {
                historyPeriod = HISTO_DAY
                limit = 30
            }
            MONTH3 -> {
                historyPeriod = HISTO_DAY
                limit = 90
            }
            YEAR -> {
                historyPeriod = HISTO_DAY
                aggregate = 13
            }
            ALL -> {
                historyPeriod = HISTO_DAY
                limit = 2000
                aggregate = 30
            }
        }
        apiService.getChartData(historyPeriod, symbol, limit, aggregate)
            .enqueue(object : Callback<ChartData> {
                override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {
                    val dataFull = response.body()!!
                    val data1 = dataFull.data
                    val data2 = dataFull.data.maxByOrNull { it.close.toFloat() }
                    val returningData = Pair(data1, data2)
                    apiCallback.onSuccess(returningData)
                }

                override fun onFailure(call: Call<ChartData>, t: Throwable) {
                    apiCallback.onError(t.message!!)
                }
            })
    }

    interface ApiCallback<T> {
        fun onSuccess(data: T)

        fun onError(errorMessage: String)
    }

}