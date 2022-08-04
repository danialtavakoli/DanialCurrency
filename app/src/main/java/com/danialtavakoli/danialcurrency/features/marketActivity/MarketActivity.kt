package com.danialtavakoli.danialcurrency.features.marketActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.danialtavakoli.danialcurrency.apiManager.ApiManager
import com.danialtavakoli.danialcurrency.apiManager.model.CoinAboutData
import com.danialtavakoli.danialcurrency.apiManager.model.CoinAboutItem
import com.danialtavakoli.danialcurrency.apiManager.model.CoinsData
import com.danialtavakoli.danialcurrency.databinding.ActivityMarketBinding
import com.danialtavakoli.danialcurrency.features.coinActivity.CoinActivity
import com.google.gson.Gson

const val URL_COIN_WATCH = "https://www.livecoinwatch.com/"

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallBack {
    private lateinit var binding: ActivityMarketBinding

    private lateinit var dataNews: ArrayList<Pair<String, String>>

    private var aboutDataMap = mutableMapOf<String, CoinAboutItem>()

    private val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutWatchlist.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL_COIN_WATCH))
            startActivity(intent)
        }
        binding.swipeRefreshMain.setOnRefreshListener {
            initUI()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshMain.isRefreshing = false
            }, 1500)
        }
        getAboutDataFromAssets()
    }

    override fun onResume() {
        super.onResume()
        initUI()
    }

    private fun initUI() {
        getNewsFromApi()
        getTopCoinsFromApi()
    }

    private fun getAboutDataFromAssets() {
        val fileInString = applicationContext.assets.open("currencyinfo.json")
            .bufferedReader().use { it.readText() }
        aboutDataMap = mutableMapOf()
        val dataAboutAll = Gson().fromJson(fileInString, CoinAboutData::class.java)
        dataAboutAll.forEach {
            aboutDataMap[it.currencyName] = CoinAboutItem(
                it.info.web,
                it.info.github,
                it.info.twt,
                it.info.desc,
                it.info.reddit
            )
        }
    }

    private fun getNewsFromApi() {
        apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                dataNews = data
                refreshNews()
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getTopCoinsFromApi() {
        apiManager.getCoinsList(object : ApiManager.ApiCallback<List<CoinsData.Data>> {
            override fun onSuccess(data: List<CoinsData.Data>) {
                showDataInRecycler(data)
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun refreshNews() {
        val randomAccess = (0..49).random()
        binding.layoutNews.txtNews.text = dataNews[randomAccess].first
        binding.layoutNews.imgNews.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataNews[randomAccess].second))
            startActivity(intent)
        }
        binding.layoutNews.txtNews.setOnClickListener { refreshNews() }
    }

    private fun showDataInRecycler(data: List<CoinsData.Data>) {
        val marketAdapter = MarketAdapter(ArrayList(data), this)
        binding.layoutWatchlist.recyclerMain.adapter = marketAdapter
        binding.layoutWatchlist.recyclerMain.layoutManager = LinearLayoutManager(this)
    }

    override fun onCoinItemClicked(data: CoinsData.Data) {
        val intent = Intent(this, CoinActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("bundle1", data)
        bundle.putParcelable("bundle2", aboutDataMap[data.coinInfo.name])
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }
}