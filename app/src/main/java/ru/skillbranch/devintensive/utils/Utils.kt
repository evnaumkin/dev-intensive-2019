package ru.skillbranch.devintensive.utils

object Utils {
    fun cyr2lat(ch: String): String {

        //val char:String

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

        /*val nc: String? = when {
            firstName.isNullOrBlank() and lastName.isNullOrBlank() -> null
            firstName.isNullOrBlank() -> lastName?.trimIndent()?.first()?.toString()
            lastName.isNullOrBlank() -> firstName?.trimIndent()?.first()?.toString()
            else -> "${firstName?.trimIndent()?.first()}${lastName?.trimIndent()?.first()}".toString()
        }
        return nc?.toUpperCase()*/
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
}