package ru.skillbranch.devintensive.extensions

fun String.truncate(value:Int = 16): String {
    var tmpStr = this.trim()
    return when {
        tmpStr.length <= value -> tmpStr
        else -> tmpStr.substring(0,value).trimEnd().plus("...")
    }
}
fun String.stripHtml(): String {
    return this.replace(Regex("<.*?>"),"")
               .replace(Regex("&([a-zA-Z]+);"),"")
               .replace(Regex("[~'<>\"^&]"),"")
               .replace("\\s+".toRegex(), " ")
               .trim()
}
