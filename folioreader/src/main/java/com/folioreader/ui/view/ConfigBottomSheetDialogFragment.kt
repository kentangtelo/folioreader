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
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.folioreader.util.StyleableTextView
import com.folioreader.util.UiUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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

    // View references
    private lateinit var container: ConstraintLayout
    private lateinit var viewConfigFontSize: TextView
    private lateinit var viewConfigFontSizeBtnIncrease: ImageButton
    private lateinit var viewConfigFontSizeBtnDecrease: ImageButton
    private lateinit var viewConfigFontTypeLatin: View
    private lateinit var viewConfigIbDayMode: ImageButton
    private lateinit var viewConfigIbNightMode: ImageButton
    private lateinit var buttonVertical: StyleableTextView
    private lateinit var buttonHorizontal: StyleableTextView
    private lateinit var viewConfigFontLatinSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_config, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view references
        container = view.findViewById(R.id.container)
        viewConfigFontSize = view.findViewById(R.id.view_config_fontSize)
        viewConfigFontSizeBtnIncrease = view.findViewById(R.id.view_config_font_size_btn_increase)
        viewConfigFontSizeBtnDecrease = view.findViewById(R.id.view_config_font_size_btn_decrease)
        viewConfigFontTypeLatin = view.findViewById(R.id.view_config_font_type_latin)
        viewConfigIbDayMode = view.findViewById(R.id.view_config_ib_day_mode)
        viewConfigIbNightMode = view.findViewById(R.id.view_config_ib_night_mode)
        buttonVertical = view.findViewById(R.id.buttonVertical)
        buttonHorizontal = view.findViewById(R.id.buttonHorizontal)
        viewConfigFontLatinSpinner = view.findViewById(R.id.view_config_font_latin_spinner)

        if (activity is FolioActivity)
            activityCallback = activity as FolioActivity

        config = AppUtil.getSavedConfig(activity)!!
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.viewTreeObserver?.addOnGlobalLayoutListener(null)
    }

    private fun initViews() {
        inflateView()
        configLatinFonts()
        viewConfigFontSize.text = config.fontSize.toString()
        configFontSizeButtons()
        selectFontLatin(config.fontLatin)

        isNightMode = config.isNightMode
        if (isNightMode) {
            container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.night))
            viewConfigFontSize.setTextColor(ContextCompat.getColor(context!!, R.color.lightText))

            viewConfigFontSizeBtnIncrease.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_night_rounded_corner_background)
            viewConfigFontSizeBtnDecrease.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_night_rounded_corner_background)

            UiUtil.setColorResToDrawable(R.color.lightText, viewConfigFontSizeBtnIncrease.drawable)
            UiUtil.setColorResToDrawable(R.color.lightText, viewConfigFontSizeBtnDecrease.drawable)

            viewConfigFontTypeLatin.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_night_rounded_corner_background)
        } else {
            container.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
            viewConfigFontSize.setTextColor(ContextCompat.getColor(context!!, R.color.night))

            viewConfigFontSizeBtnIncrease.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_day_rounded_corner_background)
            viewConfigFontSizeBtnDecrease.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_day_rounded_corner_background)

            UiUtil.setColorResToDrawable(R.color.night, viewConfigFontSizeBtnIncrease.drawable)
            UiUtil.setColorResToDrawable(R.color.night, viewConfigFontSizeBtnDecrease.drawable)

            viewConfigFontTypeLatin.background = ContextCompat.getDrawable(context!!, R.drawable.buttons_day_rounded_corner_background)
        }

        if (isNightMode) {
            viewConfigIbDayMode.isSelected = false
            viewConfigIbNightMode.isSelected = true

        } else {
            viewConfigIbDayMode.isSelected = true
            viewConfigIbNightMode.isSelected = false
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun inflateView() {

        if (config.allowedDirection != Config.AllowedDirection.VERTICAL_AND_HORIZONTAL) {
            buttonVertical.visibility = View.GONE
            buttonHorizontal.visibility = View.GONE
        }

        viewConfigIbDayMode.setOnClickListener {
            isNightMode = true
            toggleBlackTheme()
            viewConfigIbDayMode.isSelected = true
            viewConfigIbNightMode.isSelected = false
            setToolBarColor()
            setAudioPlayerBackground()

            dialog?.hide()
        }

        viewConfigIbNightMode.setOnClickListener {
            isNightMode = false
            toggleBlackTheme()
            viewConfigIbDayMode.isSelected = false
            viewConfigIbNightMode.isSelected = true

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

    @SuppressLint("ResourceAsColor")
    private fun configLatinFonts(){
        val colorStateList = UiUtil.getColorList(
            config.currentThemeColor,
            ContextCompat.getColor(context!!, R.color.grey_color)
        )

        buttonVertical.setTextColor(colorStateList)
        buttonHorizontal.setTextColor(colorStateList)

        val adapter = FontLatinAdapter(context!!,config)

        viewConfigFontLatinSpinner.adapter = adapter

        viewConfigFontLatinSpinner.background.setColorFilter(
            if (config.isNightMode) {
                R.color.night_default_font_color
            } else {
                R.color.day_default_font_color
            },
            PorterDuff.Mode.SRC_ATOP
        )

        val fontIndex = adapter.fontLatinKeyList.indexOf(config.fontLatin)
        println("configLatinFonts fontIndex:$fontIndex " )

        viewConfigFontLatinSpinner.setSelection(if (fontIndex < 0) 0 else fontIndex)

        viewConfigFontLatinSpinner.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{
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
        viewConfigFontSizeBtnDecrease.setOnClickListener {
            if (config.fontSize > 1) {
                config.fontSize -= 1
                viewConfigFontSize.text = config.fontSize.toString()

                debounce {
                    AppUtil.saveConfig(activity, config)
                    EventBus.getDefault().post(ReloadDataEvent())
                }
            }
        }

        viewConfigFontSizeBtnIncrease.setOnClickListener {
            if (config.fontSize < 10) {
                config.fontSize += 1
                viewConfigFontSize.text = config.fontSize.toString()

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
