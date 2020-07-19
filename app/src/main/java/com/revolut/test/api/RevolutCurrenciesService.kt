package com.revolut.test.api

import com.revolut.test.models.RevolutCurrencyModel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable;

interface RevolutCurrenciesService {

    @GET("latest")
    fun revolutLatest(@Query("base") base: String) : Observable<RevolutCurrencyModel>

    companion object {
        fun create(baseURL:String): RevolutCurrenciesService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build()

            return retrofit.create(RevolutCurrenciesService::class.java)
        }
    }

}

