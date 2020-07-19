package com.revolut.test.helper

import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class FormatRate {

    fun formatRate(baseRateUnit: BigDecimal):String{
        // the format depends from the current Locale as Revolut App does
        return  String.format(Locale.getDefault(),"%,.2f", baseRateUnit.setScale(2, RoundingMode.HALF_DOWN));
    }

    @Throws(JSONException::class)
    fun jsonToMap(t: String?):HashMap<String,String> {
        val map = HashMap<String, String>()
        val jObject = JSONObject(t)
        val keys: Iterator<*> = jObject.keys()
        while (keys.hasNext()) {
            val key = keys.next() as String
            val value = jObject.getString(key)
            map[key] = value
        }
        return map
    }
}