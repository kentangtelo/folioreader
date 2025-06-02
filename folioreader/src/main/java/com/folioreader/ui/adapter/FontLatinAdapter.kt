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

class FontLatinAdapter(
    context: Context,
    private val config: Config,
    private val systemFonts: Map<String, File> = FontFinder.getSystemFonts(),
    val fontLatinKeyList: List<String> =
        ArrayList<String>(systemFonts.keys.toTypedArray().sorted())
):ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,fontLatinKeyList){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        println("GET VIEW LATIN CALLED!")
        println("GET VIEW LATIN CALLED!")
        val view = createTextView(position)

        if (config.isNightMode) {
            view.setTextColor(ContextCompat.getColor(context, R.color.night_default_font_color))
        } else {
            view.setTextColor(ContextCompat.getColor(context, R.color.day_default_font_color))
        }

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        println("GET DROPDOWN LATIN VIEW CALLED!!!")
        println(position)

        val view = createTextView(position)

        if (config.isNightMode){
            view.setBackgroundResource(R.color.night_background_color)
            view.setTextColor(ContextCompat.getColor(context, R.color.night_default_font_color))
        }else{
            view.setBackgroundResource(R.color.day_background_color)
            view.setTextColor(ContextCompat.getColor(context, R.color.day_default_font_color))
        }

        return view
    }


    @SuppressLint("ViewHolder","InflateParams")
    private fun createTextView(position: Int):TextView{
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.item_styled_text,null) as TextView

        val fontKey = fontLatinKeyList[position]

        println("FONTKEY systemeFonts: $fontKey")
//        println("FONTKEY LIST: -----------------"  )
//        println(fontLatinKeyList)
//        println("SystemFonts LIST: -----------------"  )
//        println(systemFonts)

        view.text = fontKey

        println("createTextView" + systemFonts[fontKey])
        view.typeface = Typeface.createFromFile(systemFonts[fontKey])
        view.textSize = 21F

        return view
    }


}