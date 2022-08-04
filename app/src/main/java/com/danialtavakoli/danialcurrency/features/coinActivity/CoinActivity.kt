package com.danialtavakoli.danialcurrency.features.coinActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.danialtavakoli.danialcurrency.R
import com.danialtavakoli.danialcurrency.apiManager.*
import com.danialtavakoli.danialcurrency.apiManager.model.ChartData
import com.danialtavakoli.danialcurrency.apiManager.model.CoinAboutItem
import com.danialtavakoli.danialcurrency.apiManager.model.CoinsData
import com.danialtavakoli.danialcurrency.databinding.ActivityCoinBinding

class CoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoinBinding

    private lateinit var dataCoin: CoinsData.Data

    private lateinit var dataCoinAbout: CoinAboutItem

    private val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fromIntent = intent.getBundleExtra("bundle")!!
        dataCoin = fromIntent.getParcelable("bundle1")!!
        dataCoinAbout = if (fromIntent.getParcelable<CoinAboutItem>("bundle2") != null)
            fromIntent.getParcelable("bundle2")!!
        else CoinAboutItem()
        binding.layoutToolbar.toolbar.title = dataCoin.coinInfo.fullName
        initUI()
    }

    private fun initUI() {
        initAboutUI()
        initStatisticsUI()
        initChartUI()
    }

    @SuppressLint("SetTextI18n")
    private fun initChartUI() {
        var period = HOUR
        requestAndShowData(period)
        with(binding.layoutChart) {
            radioGroupMain.setOnCheckedChangeListener { _, checkedID ->
                when (checkedID) {
                    radio12h.id -> {
                        period = HOUR
                    }
                    radio1d.id -> {
                        period = HOURS24
                    }
                    radio1w.id -> {
                        period = WEEK
                    }
                    radio1m.id -> {
                        period = MONTH
                    }
                    radio3m.id -> {
                        period = MONTH3
                    }
                    radio1y.id -> {
                        period = YEAR
                    }
                    radioAll.id -> {
                        period = ALL
                    }
                }
                requestAndShowData(period)
            }
            txtChartPrice.text = dataCoin.dISPLAY.uSD.pRICE
            txtChartChange1.text = " " + dataCoin.dISPLAY.uSD.cHANGE24HOUR
            txtChartChange2.text = dataCoin.dISPLAY.uSD.cHANGEPCT24HOUR + "%"
            val changePriceRaw = dataCoin.rAW.uSD.cHANGEPCT24HOUR
            val textColor: Int
            if (changePriceRaw > 0) {
                textColor = ContextCompat.getColor(binding.root.context, R.color.colorGain)
                txtChartChange2.setTextColor(textColor)
                txtChartUpdown.setTextColor(textColor)
                sparkViewMain.lineColor = textColor
                txtChartUpdown.text = "▲"
            } else if (changePriceRaw < 0) {
                textColor = ContextCompat.getColor(binding.root.context, R.color.colorLoss)
                txtChartChange2.setTextColor(textColor)
                txtChartUpdown.setTextColor(textColor)
                sparkViewMain.lineColor = textColor
                txtChartUpdown.text = "▼"
            }
            sparkViewMain.setScrubListener {
                if (it == null) txtChartPrice.text = dataCoin.dISPLAY.uSD.pRICE
                else txtChartPrice.text = "$" + (it as ChartData.Data).close.toString()
            }
        }
    }

    private fun requestAndShowData(period: String) {
        apiManager.getChartData(
            dataCoin.coinInfo.name,
            period,
            object : ApiManager.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {
                    val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                    binding.layoutChart.sparkViewMain.adapter = chartAdapter
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(this@CoinActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun initStatisticsUI() {
        with(binding.layoutStatistics) {
            tvOpenAmount.text = dataCoin.dISPLAY.uSD.oPEN24HOUR
            tvTodaysHighAmount.text = dataCoin.dISPLAY.uSD.hIGH24HOUR
            tvTodayLowAmount.text = dataCoin.dISPLAY.uSD.lOW24HOUR
            tvChangeTodayAmount.text = dataCoin.dISPLAY.uSD.cHANGE24HOUR
            tvAlgorithm.text = dataCoin.coinInfo.algorithm
            tvTotalVolume.text = dataCoin.dISPLAY.uSD.tOTALVOLUME24H
            tvAvgMarketCapAmount.text = dataCoin.dISPLAY.uSD.mKTCAP
            tvSupplyNumber.text = dataCoin.dISPLAY.uSD.sUPPLY
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initAboutUI() {
        with(binding.layoutAbout) {
            txtWebsite.text = dataCoinAbout.coinWebsite
            txtGithub.text = dataCoinAbout.coinGithub
            txtReddit.text = dataCoinAbout.coinReddit
            txtTwitter.text = "@" + dataCoinAbout.coinTwitter
            txtAboutCoin.text = dataCoinAbout.coinDesc
            txtWebsite.setOnClickListener { openWebsiteDataCoin(dataCoinAbout.coinWebsite!!) }
            txtGithub.setOnClickListener { openWebsiteDataCoin(dataCoinAbout.coinGithub!!) }
            txtReddit.setOnClickListener { openWebsiteDataCoin(dataCoinAbout.coinReddit!!) }
            txtTwitter.setOnClickListener { openWebsiteDataCoin(BASE_URL_TWITTER + dataCoinAbout.coinTwitter!!) }
        }
    }

    private fun openWebsiteDataCoin(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}