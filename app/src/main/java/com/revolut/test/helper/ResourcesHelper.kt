package com.revolut.test.helper

import android.content.Context

class ResourcesHelper {

    fun currencyNameResId(context: Context, symbol: String) =
        context.resources.getIdentifier("currency_" + symbol + "_name", "string",
            context.packageName)

    fun currencyFlagResId(context: Context, symbol: String) = context.resources.getIdentifier(
        "ic_" + symbol + "_flag", "mipmap", context.packageName)
}