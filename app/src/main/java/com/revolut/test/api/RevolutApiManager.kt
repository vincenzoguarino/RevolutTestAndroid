package com.revolut.test.api

import com.google.gson.Gson
import com.revolut.test.models.RevolutCurrencyModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class RevolutApiManager private constructor() {

    var disposable: Disposable? = null
    private val gson = Gson()
    var baseCurrency = RevolutCurrencyModel().baseCurrency

    val revolutApiService by lazy {

        RevolutCurrenciesService.create(RevolutApiManager.Companion.baseURL)}

    private object HOLDER {
        val INSTANCE = RevolutApiManager()

    }

    companion object {

        val instance: RevolutApiManager by lazy { HOLDER.INSTANCE }
        val baseURL = "https://hiring.revolut.codes/api/android/"
    }

    fun updateLatest(callback: (String) -> Unit) {
        disposable =
            revolutApiService.revolutLatest(baseCurrency)
                .switchMap { revolutApiService.revolutLatest(baseCurrency) }
                .defaultIfEmpty(RevolutCurrencyModel())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { completed -> completed.delay(1000, TimeUnit.MILLISECONDS) }
                .subscribe(
                    { result ->

                        val fromUserJson = gson.toJson(result)
                        callback(fromUserJson)

                    },
                    { error ->

                        println(error.message)

                    }
                )
    }

}