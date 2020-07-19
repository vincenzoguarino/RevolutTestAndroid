package com.revolut.test.models

import java.math.BigDecimal

data class CurrencyModel(

    val name: Int = 0,
    val symbol: String = "",
    var rate: BigDecimal? = BigDecimal.ONE,
    val flag: Int = 0

)
fun CurrencyModel.calculateRate(baseRateUnit:BigDecimal): BigDecimal? {
    return rate?.times(baseRateUnit)
}

