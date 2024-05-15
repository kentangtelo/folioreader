package com.folioreader.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.folioreader.Config
import com.folioreader.R
import com.folioreader.util.FontFinder
import java.io.File

class FontArabicAdapter(
    private val config: Config,
    context: Context,
    private val userFonts: Map<String, File> = FontFinder.getAssetsFonts(context),
    val fontArabicKeyList: List<String> =
        ArrayList<String>(userFonts.keys.toTypedArray().sorted())

) : ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, fontArabicKeyList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        println("GET VIEW ARABIC CALLED!")
        println("GET VIEW ARABIC CALLED!")
        val view = createTextView(position)

        if (config.isNightMode) {
            view.setTextColor(ContextCompat.getColor(context, R.color.night_default_font_color))
        } else {
            view.setTextColor(ContextCompat.getColor(context, R.color.day_default_font_color))
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        println("GET DROP DOWN ARABIC VIEW CALLED!")
        println(position)

        val view = createTextView(position)

        if (config.isNightMode) {
            view.setBackgroundResource(R.color.night_background_color)
            view.setTextColor(ContextCompat.getColor(context, R.color.night_default_font_color))
        } else {
            view.setBackgroundResource(R.color.day_background_color)
            view.setTextColor(ContextCompat.getColor(context, R.color.day_default_font_color))
        }

        return view
    }

    @SuppressLint("ViewHolder", "InflateParams")
    private fun createTextView(position: Int): TextView {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.item_styled_text, null) as TextView


        val fontKey = fontArabicKeyList[position]
        println("FONTKEY userFonts: $fontKey")
//        println("FONTKEY LIST: -----------------"  )
//        println(fontArabicKeyList)
//        println("UserFonts LIST: -----------------"  )
//        println(userFonts)

        view.text = fontKey

//        if (userFonts.containsKey(fontKey)) {
        println("createTextView" + userFonts[fontKey])
        view.typeface = Typeface.createFromFile(userFonts[fontKey])
        view.textSize = 21F
//        } else if (systemFonts.containsKey(fontKey)) {
//            view.typeface = Typeface.createFromFile(systemFonts[fontKey])
//        }

        return view
    }
}