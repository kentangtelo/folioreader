package com.folioreader.ui.view

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.folioreader.Config
import com.folioreader.R
import com.folioreader.model.event.ReloadDataEvent
import com.folioreader.ui.activity.FolioActivity
import com.folioreader.ui.activity.FolioActivityCallback
import com.folioreader.ui.adapter.FontArabicAdapter
import com.folioreader.ui.adapter.FontLatinAdapter
import com.folioreader.ui.fragment.MediaControllerFragment
import com.folioreader.util.AppUtil
import com.folioreader.util.UiUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.view_config.*
import org.greenrobot.eventbus.EventBus
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created by mobisys2 on 11/16/2016.
 */
class ConfigBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val FADE_DAY_NIGHT_MODE = 10

        @JvmField
        val LOG_TAG: String = ConfigBottomSheetDialogFragment::class.java.simpleName
    }

    private lateinit var config: Config
    private var isNightMode = false
    private lateinit var activityCallback: FolioActivityCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_config, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is FolioActivity)
            activityCallback = activity as FolioActivity

//        view.viewTreeObserver.addOnGlobalLayoutListener {
//            val dialog = dialog as BottomSheetDialog
//            val bottomSheet =
//                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
//            val behavior = BottomSheetBehavior.from(bottomSheet!!)
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.peekHeight = 500
//        }

        config = AppUtil.getSavedConfig(activity)!!
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.viewTreeObserver?.addOnGlobalLayoutListener(null)
    }

    private fun initViews() {
        inflateView()
//        configArabicFonts()
        configLatinFonts()
        view_config_fontSize.text = config.fontSize.toString()
        configFontSizeButtons()
//        selectFontArabic(config.fontArabic)
        selectFontLatin(config.fontLatin)

        isNightMode = config.isNightMode
        if (isNightMode) {
            container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.night))
            view_config_fontSize.setTextColor(ContextCompat.getColor(context!!, R.color.lightText))

            view_config_font_size_btn_increase.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_night_rounded_corner_background)
            view_config_font_size_btn_decrease.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_night_rounded_corner_background)

            UiUtil.setColorResToDrawable(R.color.lightText, view_config_font_size_btn_increase.drawable)
            UiUtil.setColorResToDrawable(R.color.lightText, view_config_font_size_btn_decrease.drawable)

//            view_config_font_type.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_night_rounded_corner_background)
            view_config_font_type_latin.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_night_rounded_corner_background)
        } else {
            container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
            view_config_fontSize.setTextColor(ContextCompat.getColor(context!!, R.color.night))

            view_config_font_size_btn_increase.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_day_rounded_corner_background)
            view_config_font_size_btn_decrease.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_day_rounded_corner_background)

            UiUtil.setColorResToDrawable(R.color.night, view_config_font_size_btn_increase.drawable)
            UiUtil.setColorResToDrawable(R.color.night, view_config_font_size_btn_decrease.drawable)

//            view_config_font_type.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_day_rounded_corner_background)
            view_config_font_type_latin.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_day_rounded_corner_background)
        }

        if (isNightMode) {
            view_config_ib_day_mode.isSelected = false
            view_config_ib_night_mode.isSelected = true

        } else {
            view_config_ib_day_mode.isSelected = true
            view_config_ib_night_mode.isSelected = false
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun inflateView() {

        if (config.allowedDirection != Config.AllowedDirection.VERTICAL_AND_HORIZONTAL) {
//            view5.visibility = View.GONE
            buttonVertical.visibility = View.GONE
            buttonHorizontal.visibility = View.GONE
        }

        view_config_ib_day_mode.setOnClickListener {
            isNightMode = true
            toggleBlackTheme()
            view_config_ib_day_mode.isSelected = true
            view_config_ib_night_mode.isSelected = false
            setToolBarColor()
            setAudioPlayerBackground()

            dialog?.hide()
        }

        view_config_ib_night_mode.setOnClickListener {
            isNightMode = false
            toggleBlackTheme()
            view_config_ib_day_mode.isSelected = false
            view_config_ib_night_mode.isSelected = true

            setToolBarColor()
            setAudioPlayerBackground()
            dialog?.hide()
        }

        if (activityCallback.direction == Config.Direction.HORIZONTAL) {
            buttonHorizontal.isSelected = true
        } else if (activityCallback.direction == Config.Direction.VERTICAL) {
            buttonVertical.isSelected = true
        }

        buttonVertical.setOnClickListener {
            config = AppUtil.getSavedConfig(context)!!
            config.direction = Config.Direction.VERTICAL
            AppUtil.saveConfig(context, config)
            activityCallback.onDirectionChange(Config.Direction.VERTICAL)
            buttonHorizontal.isSelected = false
            buttonVertical.isSelected = true
        }

        buttonHorizontal.setOnClickListener {
            config = AppUtil.getSavedConfig(context)!!
            config.direction = Config.Direction.HORIZONTAL
            AppUtil.saveConfig(context, config)
            activityCallback.onDirectionChange(Config.Direction.HORIZONTAL)
            buttonHorizontal.isSelected = true
            buttonVertical.isSelected = false
        }
    }


    private var fontChangedArabic = false
    private var fontChangedLatin = false
//    @SuppressLint("ResourceAsColor")
//    private fun configArabicFonts() {
//        val colorStateList = UiUtil.getColorList(
//            config.currentThemeColor,
//            ContextCompat.getColor(context!!, R.color.grey_color)
//        )
//
//        buttonVertical.setTextColor(colorStateList)
//        buttonHorizontal.setTextColor(colorStateList)
//
//        val adapter = FontArabicAdapter(config, context!!)
//
//        view_config_font_spinner.adapter = adapter
//
//        view_config_font_spinner.background.setColorFilter(
//            if (config.isNightMode) {
//                R.color.night_default_font_color
//            } else {
//                R.color.day_default_font_color
//            },
//            PorterDuff.Mode.SRC_ATOP
//        )
//
//        val fontIndex = adapter.fontArabicKeyList.indexOf(config.fontArabic)
//        println("configArabicFonts fontIndex:$fontIndex " )
//        view_config_font_spinner.setSelection(if (fontIndex < 0) 0 else fontIndex)
//
//        view_config_font_spinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    val selectedFont = adapter.fontArabicKeyList[position]
//                    selectFontArabic(selectedFont)
//                    fontChangedArabic = true // Set the fontChanged flag
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                }
//            }
//    }
//
//    private fun selectFontArabic(selectedFont: String) {
//        // parse font from name
//        config.fontArabic = selectedFont
//        AppUtil.saveConfig(activity, config)
//
//        // Check if the font has changed and post ReloadDataEvent if necessary
//        if (fontChangedArabic) {
//            EventBus.getDefault().post(ReloadDataEvent())
//            fontChangedArabic = false // Reset the flag
//        }
//    }

    @SuppressLint("ResourceAsColor")
    private fun configLatinFonts(){
        val colorStateList = UiUtil.getColorList(
            config.currentThemeColor,
            ContextCompat.getColor(context!!, R.color.grey_color)
        )

        buttonVertical.setTextColor(colorStateList)
        buttonHorizontal.setTextColor(colorStateList)

        val adapter = FontLatinAdapter(context!!,config)

        view_config_font_latin_spinner.adapter = adapter

        view_config_font_latin_spinner.background.setColorFilter(
            if (config.isNightMode) {
                R.color.night_default_font_color
            } else {
                R.color.day_default_font_color
            },
            PorterDuff.Mode.SRC_ATOP
        )

        val fontIndex = adapter.fontLatinKeyList.indexOf(config.fontLatin)
        println("configLatinFonts fontIndex:$fontIndex " )

        view_config_font_latin_spinner.setSelection(if (fontIndex < 0) 0 else fontIndex)

        view_config_font_latin_spinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                val selectedFont = adapter.fontLatinKeyList[position]
                selectFontLatin(selectedFont)
                fontChangedLatin = true
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


    }

    private fun selectFontLatin(selectedFont: String){
        config.fontLatin = selectedFont
        AppUtil.saveConfig(activity,config)

        // Trigger reload  untuk mengganti font di epub
        if (fontChangedLatin){
            EventBus.getDefault().post(ReloadDataEvent())

            fontChangedLatin = false // Reset the flag
        }
    }

    private fun toggleBlackTheme() {

        val day = ContextCompat.getColor(context!!, R.color.white)
        val night = ContextCompat.getColor(context!!, R.color.night)

        val colorAnimation = ValueAnimator.ofObject(
            ArgbEvaluator(),
            if (isNightMode) night else day, if (isNightMode) day else night
        )
        colorAnimation.duration = FADE_DAY_NIGHT_MODE.toLong()

        colorAnimation.addUpdateListener { animator ->
            val value = animator.animatedValue as Int
            container.setBackgroundColor(value)
        }

        colorAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}

            override fun onAnimationEnd(animator: Animator) {
                isNightMode = !isNightMode
                config.isNightMode = isNightMode
                AppUtil.saveConfig(activity, config)
                EventBus.getDefault().post(ReloadDataEvent())
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })

        colorAnimation.duration = FADE_DAY_NIGHT_MODE.toLong()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val attrs = intArrayOf(android.R.attr.navigationBarColor)
            val typedArray = activity?.theme?.obtainStyledAttributes(attrs)
            val defaultNavigationBarColor = typedArray?.getColor(
                0,
                ContextCompat.getColor(context!!, R.color.white)
            )
            val black = ContextCompat.getColor(context!!, R.color.black)

            val navigationColorAnim = ValueAnimator.ofObject(
                ArgbEvaluator(),
                if (isNightMode) black else defaultNavigationBarColor,
                if (isNightMode) defaultNavigationBarColor else black
            )

            navigationColorAnim.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Int
                activity?.window?.navigationBarColor = value
            }

            navigationColorAnim.duration = FADE_DAY_NIGHT_MODE.toLong()
            navigationColorAnim.start()
        }

        colorAnimation.start()
    }

    private var debounceFuture: ScheduledFuture<*>? = null
    private val debounceExecutor = Executors.newSingleThreadScheduledExecutor()

    private fun configFontSizeButtons() {
        view_config_font_size_btn_decrease.setOnClickListener {
            if (config.fontSize > 1) {
                config.fontSize -= 1
                view_config_fontSize.text = config.fontSize.toString()

                debounce {
                    AppUtil.saveConfig(activity, config)
                    EventBus.getDefault().post(ReloadDataEvent())
                }
            }
        }

        view_config_font_size_btn_increase.setOnClickListener {
            if (config.fontSize < 10) {
                config.fontSize += 1
                view_config_fontSize.text = config.fontSize.toString()

                debounce {
                    AppUtil.saveConfig(activity, config)
                    EventBus.getDefault().post(ReloadDataEvent())
                    print("DEBOUNCE EXECUTEDD!!")
                }
            }
        }
    }

    fun debounce(
        delayMillis: Long = 300,
        action: () -> Unit
    ) {
        // Cancel any previous debounce task
        debounceFuture?.cancel(false)

        // Schedule a new debounce task
        debounceFuture = debounceExecutor.schedule({
            action()
        }, delayMillis, TimeUnit.MILLISECONDS)
    }


    private fun setToolBarColor() {
        if (isNightMode) {
            activityCallback.setDayMode()
        } else {
            activityCallback.setNightMode()
        }
    }

    private fun setAudioPlayerBackground() {

        var mediaControllerFragment: Fragment? =
            fragmentManager?.findFragmentByTag(MediaControllerFragment.LOG_TAG)
                ?: return
        mediaControllerFragment = mediaControllerFragment as MediaControllerFragment
        if (isNightMode) {
            mediaControllerFragment.setDayMode()
        } else {
            mediaControllerFragment.setNightMode()
        }
    }
}
