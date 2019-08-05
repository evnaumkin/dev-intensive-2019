package ru.skillbranch.devintensive.utils

import android.content.Context
import android.util.TypedValue

object Utils {
    fun cyr2lat(ch: String): String {

        return when(ch){
            "а" -> "a"
            "б" -> "b"
            "в" -> "v"
            "г" -> "g"
            "д" -> "d"
            "е" -> "e"
            "ё" -> "e"
            "ж" -> "zh"
            "з" -> "z"
            "и" -> "i"
            "й" -> "i"
            "к" -> "k"
            "л" -> "l"
            "м" -> "m"
            "н" -> "n"
            "о" -> "o"
            "п" -> "p"
            "р" -> "r"
            "с" -> "s"
            "т" -> "t"
            "у" -> "u"
            "ф" -> "f"
            "х" -> "h"
            "ц" -> "c"
            "ч" -> "ch"
            "ш" -> "sh"
            "щ" -> "sh'"
            "ь" -> ""
            "ы" -> "i"
            "ъ" -> ""
            "э" -> "e"
            "ю" -> "yu"
            "я" -> "ya"
            else -> ch
        }
    }

    fun parseFullName(fullName: String?): Pair<String?, String?> {

        if (fullName.isNullOrBlank()) { return Pair(null, null) }

        val tmpName:String = fullName.trim()

        val firstName = tmpName.split(" ")[0]
        val lastName = tmpName.replace(firstName, "").trim()

        if (lastName.isNullOrBlank()) { return Pair(firstName, null) }

        return Pair(firstName, lastName)
    }

    fun toInitials(firstName: String?, lastName: String?): String? {

        if (firstName.isNullOrBlank() and lastName.isNullOrBlank()) { return null }

        return listOf(firstName, lastName).filter { !it.isNullOrBlank() }
                                          .map { it?.trimStart()?.first() }
                                          .joinToString("")
                                          .toUpperCase()
    }

    fun transliteration(payload: String, divider: String = " "): String {

        var originalStr: String = payload.split(" ")
                                         .filter { !it.isNullOrBlank() }
                                         .joinToString(divider)

        val sb = StringBuilder()
        var strLat:String
        var charLower:Char
        for(char in originalStr){
            charLower = char.toLowerCase()
            strLat = cyr2lat(charLower.toString())
            if (!char.equals(charLower)) { strLat = strLat.capitalize() }
            sb.append(strLat);
        }
        return sb.toString();
    }

    private val exceptionsAddresses = listOf(
            "enterprise",
            "features",
            "topics",
            "collections",
            "trending",
            "events",
            "marketplace",
            "pricing",
            "nonprofit",
            "customer-stories",
            "security",
            "login",
            "join")

    fun validateRepository(repository: String): Boolean {
        if (repository.isEmpty()) return true
        val regex = Regex("^(https:\\/\\/)?(www\\.)?(github\\.com\\/)(?!(${exceptionsAddresses.joinToString("|")})(?=\\/|\$))[a-zA-Z\\d](?:[a-zA-Z\\d]|-(?=[a-zA-Z\\d])){0,38}(\\/)?$")
        return repository.contains(regex)
    }

    fun convertDpToPixels(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
    fun convertPixelsToDp(context: Context, pixels: Int): Int {
        return pixels.div(context.resources.displayMetrics.density).toInt()
    }
    fun convertSpToPixels(context: Context, sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
    }

    //fun dp2px(context: Context, dp: Float) : Float = (dp * context.resources.displayMetrics.density + 0.5f)
    //fun sp2px(context: Context, sp: Float) : Float = (sp * context.resources.displayMetrics.density)
    //fun px2dp(context: Context, pixels: Float) : Float = (pixels / context.resources.displayMetrics.density + 0.5f)

}