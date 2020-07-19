package com.revolut.test.models

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.revolut.test.helper.FormatRate
import com.revolut.test.helper.ResourcesHelper


data class RevolutCurrencyModel (

    @SerializedName("baseCurrency") val baseCurrency : String = "EUR",
    @SerializedName("rates") val rates: Rates = Rates()

)

fun RevolutCurrencyModel.createCurrenciesModelArray(map:String, context: Context): MutableList<CurrencyModel> {

    var arrayCurriences: MutableList<CurrencyModel> = mutableListOf()

    var mapCurrencies = FormatRate().jsonToMap(map)
    var  currentRates = FormatRate().jsonToMap(mapCurrencies["rates"])

    //add default base currency on top
    arrayCurriences.add(
        CurrencyModel(
            ResourcesHelper().currencyNameResId(
                context,
                RevolutCurrencyModel().baseCurrency.toLowerCase()
            ),
            RevolutCurrencyModel().baseCurrency,
            rates.eur,
            ResourcesHelper()
                .currencyFlagResId(context, RevolutCurrencyModel().baseCurrency.toLowerCase())
        )
    )

    currentRates.forEach { (key, value) ->

        if (key != RevolutCurrencyModel().baseCurrency){

            arrayCurriences.add(
                CurrencyModel(
                    ResourcesHelper().currencyNameResId(
                        context,
                        key.toLowerCase()
                    ),
                    key,
                    value.toBigDecimal(),
                    ResourcesHelper()
                        .currencyFlagResId(context, key.toLowerCase())
                )
            )

        }

    }

    return arrayCurriences

}

