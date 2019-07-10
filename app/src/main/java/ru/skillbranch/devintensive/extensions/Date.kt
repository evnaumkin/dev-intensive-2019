package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

enum class TimeUnits{
    SECOND{
        override fun plural(value: Int): String = when{
            value % 100 in 11..19 -> "$value секунд"
            value % 10 == 1 -> "$value секунду"
            value % 10 in 2..4 -> "$value секунды"
            else -> "$value секунд"
        }
    },
    MINUTE{
        override fun plural(value: Int): String = when{
            value % 100 in 11..19 -> "$value минут"
            value % 10 == 1 -> "$value минуту"
            value % 10 in 2..4 -> "$value минуты"
            else -> "$value минут"
        }
    },
    HOUR{
        override fun plural(value: Int): String = when{
            value % 100 in 11..19 -> "$value часов"
            value % 10 == 1 -> "$value час"
            value % 10 in 2..4 -> "$value часа"
            else -> "$value часов"
        }
    },
    DAY{
        override fun plural(value: Int): String = when{
            value % 100 in 11..19 -> "$value дней"
            value % 10 == 1 -> "$value день"
            value % 10 in 2..4 -> "$value дня"
            else -> "$value дней"
        }
    };
    abstract fun plural(value: Int): String
}

fun Date.format(pattern:String="HH:mm:ss dd.MM.yy"):String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value:Int, units: TimeUnits = TimeUnits.SECOND) : Date {
    var time = this.time

    time += when(units){
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(date:Date = Date()): String {
    var differenceSeconds: Int = ((date.time - this.time) / 1000).toInt()
    var flagFutur: Boolean = false

    if(differenceSeconds < 0) {
        differenceSeconds = (-differenceSeconds) + 1
        flagFutur = true
    }

    val differenceMinutes: Int = differenceSeconds / 60
    val differenceHours: Int = differenceSeconds / 3600
    val differenceDays: Int = differenceSeconds / 86400

    if(flagFutur) {
        return when {
            // second
            differenceSeconds <= 45 -> "через несколько секунд"
            // minute
            differenceSeconds <= 75 -> "через минуту"
            differenceMinutes <= 45 -> "через " + TimeUnits.MINUTE.plural(differenceMinutes)
            // hour
            differenceMinutes <= 75 -> "через час"
            differenceHours <= 22 -> "через " + TimeUnits.HOUR.plural(differenceHours)
            differenceHours <= 26 -> "через день"
            // day
            differenceDays <= 360 -> "через " + TimeUnits.DAY.plural(differenceDays)
            else -> "более чем через год"
        }
    }
    else{
        return when {
            // second
            differenceSeconds <= 1 -> "только что"
            differenceSeconds <= 45 -> "несколько секунд назад"
            differenceSeconds <= 75 -> "минуту назад"
            // minute
            differenceMinutes <= 45 -> TimeUnits.MINUTE.plural(differenceMinutes) + " назад"
            differenceMinutes <= 75 -> "час назад"
            // hour
            differenceHours <= 22 -> TimeUnits.HOUR.plural(differenceHours) + " назад"
            differenceHours <= 26 -> "день назад"
            // day
            differenceDays <= 360 -> TimeUnits.DAY.plural(differenceDays) + " назад"
            else -> "более года назад"
        }
    }
}
