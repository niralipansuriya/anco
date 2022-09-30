package com.app.ancoturf.data.common

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.util.*


class CalendarNumberView(context: Context?) : View(context) {

    val MAX_WEEKS_IN_MONTH = 6
    private val MAX_SELECTION_FINGER_SHIFT_DIST = 5.0f

    private var paint: TextPaint? = null
    private var cellPadding = 0
    private var textColor = 0
    private var inactiveTextColor = 0
    private var selectionTextColor = 0
    private var cellBackgroundColor = 0
    private var cellSelectionBackgroundColor = 0
    private var dayNamesTextColor = 0
    private var dayNamesBackgroundColor = 0
    private val showDayNames = true
    private val locale: Locale = Locale.getDefault()

    private var selectedDate: Calendar? = null
    private var shownMonth: Calendar? = null

    //private val listener: DateSelectionListener? = null

    //temporary and cache values
    private val _cachedCellSideWidth = 0
    private val _cachedCellSideHeight = 0
    private val _calendar: Calendar = Calendar.getInstance()
    private val _rect: Rect = Rect()
    private val _textHeight = 0f
    private val _x = 0f
    private val _y = 0f
    private val _boldTypeface: Typeface? = null
    private val _defaultTypeface: Typeface? = null

    constructor(context: Context?, attributeSet: AttributeSet) : this(context) {
        // init(attributeSet)
    }

    constructor(context: Context?, attributeSet: AttributeSet, defStyleAttr: Int) : this(
        context,
        attributeSet
    ) {
        //   init(attributeSet)
    }

    /*private fun init(attrs: AttributeSet) {
        paint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG)
        //setup defaults
        paint!!.textSize =
            resources.getDimensionPixelSize(R.dimen.calendar_default_text_size).toFloat()
        textColor = resources.getColor(R.color.calendar_default_text_color)
        inactiveTextColor = resources.getColor(R.color.calendar_default_inactive_text_color)
        selectionTextColor = resources.getColor(R.color.calendar_default_selection_text_color)
        cellPadding = resources.getDimensionPixelSize(R.dimen.calendar_default_cell_padding)
        cellBackgroundColor =
            resources.getColor(R.color.calendar_default_cell_background_color)
        cellSelectionBackgroundColor =
            resources.getColor(R.color.calendar_default_cell_selection_background_color)
        dayNamesTextColor =
            resources.getColor(R.color.calendar_default_day_names_cell_text_color)
        dayNamesBackgroundColor =
            resources.getColor(R.color.calendar_default_day_names_cell_background_color)
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.CalendarNumbersView)
        if (ta != null) {
            paint!!.textSize = ta.getDimensionPixelSize(
                R.styleable.CalendarNumbersView_fontSize,
                paint!!.textSize.toInt()
            ).toFloat()
            textColor = ta.getColor(R.styleable.CalendarNumbersView_textColor, textColor)
            inactiveTextColor =
                ta.getColor(R.styleable.CalendarNumbersView_inactiveTextColor, inactiveTextColor)
            selectionTextColor =
                ta.getColor(R.styleable.CalendarNumbersView_selectionTextColor, selectionTextColor)
            cellPadding =
                ta.getDimensionPixelSize(R.styleable.CalendarNumbersView_cellPadding, cellPadding)
            cellBackgroundColor = ta.getColor(
                R.styleable.CalendarNumbersView_cellBackgroundColor,
                cellBackgroundColor
            )
            cellSelectionBackgroundColor = ta.getColor(
                R.styleable.CalendarNumbersView_cellSelectionBackgroundColor,
                cellSelectionBackgroundColor
            )
            dayNamesTextColor = ta.getColor(
                R.styleable.CalendarNumbersView_cellDayNamesCellTextColor,
                dayNamesTextColor
            )
            dayNamesBackgroundColor = ta.getColor(
                R.styleable.CalendarNumbersView_cellDayNamesCellBackgroundColor,
                dayNamesBackgroundColor
            )
        }
        selectedDate = Calendar.getInstance()
        shownMonth = selectedDate.clone() as Calendar
    }*/
}