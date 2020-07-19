package com.revolut.test.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.revolut.test.helper.FormatRate
import com.revolut.test.R
import com.revolut.test.models.CurrencyModel
import com.revolut.test.models.RevolutCurrencyModel
import com.revolut.test.models.calculateRate
import kotlinx.android.synthetic.main.row_line_latest.view.*
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException


class AdapterLatest(
    context: Context,
    var currenciesArray: MutableList<CurrencyModel>
) :
RecyclerView.Adapter<AdapterLatest.MyViewHolder>() {

    var baseCurrency = RevolutCurrencyModel().baseCurrency
    var baseUnitValue : BigDecimal = BigDecimal.ZERO
    var numberFormat = NumberFormat.getInstance()!!

    private val df: DecimalFormat = DecimalFormat("#,###.##")
    private val dfnd: DecimalFormat = DecimalFormat("#,###")
    private var hasFractionalPart: Boolean = false

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_line_latest, parent, false)){
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        df.isDecimalSeparatorAlwaysShown = true

        return MyViewHolder(inflater, parent
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        updateView(holder,position)
    }

    private fun updateView(holder: MyViewHolder, position: Int){

        holder.itemView.currencyName.setText(currenciesArray[position].name)
        holder.itemView.currencyFlag.setImageResource(currenciesArray[position].flag)
        holder.itemView.currencySymbol.text = currenciesArray[position].symbol

        holder.itemView.currencyAmount.tag = holder.itemView.currencySymbol.text.toString()

        if (holder.itemView.currencyAmount.tag.toString() == baseCurrency){

            if(baseUnitValue != BigDecimal.ZERO){

                var currentText = FormatRate().formatRate(baseUnitValue)
                holder.itemView.currencyAmount.setText(currentText)
                holder.itemView.currencyAmount.setSelection(holder.itemView.currencyAmount.text.length);

            }

            textListenerCurrencyAmount(holder.itemView.currencyAmount)
        }else{

            currencyValueForBaseUnit(holder.itemView.currencyAmount,position,baseUnitValue)

        }

        holder.itemView.setOnClickListener {

            if (position != 0){

                var currentText = holder.itemView.currencyAmount.text.toString()

                baseCurrency = holder.itemView.currencySymbol.text.toString()

                baseUnitValue = if(currentText.isNotEmpty()){

                    val currentValue = numberFormat.parse(currentText).toDouble()
                    currentValue.toBigDecimal()

                }else{

                    BigDecimal.ZERO
                }

                if (holder.itemView.currencyAmount.tag.toString() == baseCurrency){
                    textListenerCurrencyAmount(holder.itemView.currencyAmount)
                }

                changeBaseCurrency(position)

            }

        }

    }

     private fun currencyValueForBaseUnit(editText:EditText, position:Int, baseValue:BigDecimal){

         editText.setText(currenciesArray[position].calculateRate(baseValue)?.let {
             FormatRate().formatRate(
                 it
             )
         })

        if (baseUnitValue == BigDecimal.ZERO){

            editText.setText("")
        }

    }

    private fun changeBaseCurrency(fromPosition: Int) {
        val item = currenciesArray[fromPosition]
        currenciesArray.removeAt(fromPosition);
        currenciesArray.add(0, item);

        notifyDataSetChanged()
    }

    private fun textListenerCurrencyAmount(et: EditText){
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                et.removeTextChangedListener(this)

                baseUnitValue = if (!et.text.isNullOrEmpty()) {
                    val currentValue = numberFormat.parse(et.text.toString()).toDouble()
                    currentValue.toBigDecimal()

                }else{

                    BigDecimal.ZERO
                }
                try {
                    val inilen: Int = et.text?.length ?: 0
                    val v: String = s.toString().replace(
                        java.lang.String.valueOf(
                            df.decimalFormatSymbols.groupingSeparator
                        ), ""
                    )
                    val n: Number = df.parse(v)
                    val cp = et.selectionStart
                    if (hasFractionalPart) {
                        et.setText(df.format(n))
                    } else {
                        et.setText(dfnd.format(n))
                    }
                    val endlen: Int = et.text?.length ?: 0
                    val sel = cp.plus((endlen - inilen))
                    if (sel > 0 && sel <= et.text.length) {
                        et.setSelection(sel)
                    } else {
                        // place cursor at the end?
                        et.setSelection(et.text.length - 1)
                    }
                } catch (nfe: NumberFormatException) {

                } catch (e: ParseException) {

                }
                et.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int
            ) {
                hasFractionalPart = s.toString().contains(
                    java.lang.String.valueOf(
                        df.decimalFormatSymbols.decimalSeparator
                    )
                )
            }

        })

    }

    fun updateCurrenciesRatesLive(revolutCurrencies: MutableList<CurrencyModel>){

        for (currency in revolutCurrencies){
            currenciesArray.find { it.symbol == currency.symbol}?.rate = currency.rate

        }

       notifyItemRangeChanged(1, currenciesArray.count() - 1)

    }

    override fun getItemCount() = currenciesArray.size

}


