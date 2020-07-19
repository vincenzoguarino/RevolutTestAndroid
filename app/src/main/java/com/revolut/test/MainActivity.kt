package com.revolut.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revolut.test.adapters.AdapterLatest
import com.revolut.test.api.RevolutApiManager
import com.revolut.test.models.RevolutCurrencyModel
import com.revolut.test.models.createCurrenciesModelArray
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var adapter: AdapterLatest? = null
    var isFirstCall : Boolean = false
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        initView()
        apiLatest()

        adapter?.listenerScroolToTop = {

            recyclerLatest.smoothScrollToPosition(0)

        }
    }

    private fun initView() {

        recyclerLatest.setHasFixedSize(true)
        recyclerLatest.layoutManager = LinearLayoutManager(this)
        recyclerLatest.itemAnimator?.changeDuration = 0

        adapter = AdapterLatest(
            this,
           ArrayList()
        )
        recyclerLatest.adapter = adapter

    }

    private fun apiLatest(){

            RevolutApiManager.instance.updateLatest() {

                val revolutCurrenciesModel: RevolutCurrencyModel =
                    gson.fromJson<RevolutCurrencyModel>(it, RevolutCurrencyModel::class.java)

                if (!isFirstCall) {

              runOnUiThread{

                    adapter?.currenciesArray = revolutCurrenciesModel.createCurrenciesModelArray(it,this)

                    adapter?.notifyDataSetChanged()

              }
               }

                adapter?.updateCurrenciesRatesLive(
                    revolutCurrenciesModel.createCurrenciesModelArray(
                        it,
                        context = this
                    )
                )
                RevolutApiManager.instance.baseCurrency = adapter?.baseCurrency.toString()
                isFirstCall = true

            }
    }

    override fun onPause() {
        super.onPause()
        RevolutApiManager.instance.disposable?.dispose()
        isFirstCall = false
    }

}

