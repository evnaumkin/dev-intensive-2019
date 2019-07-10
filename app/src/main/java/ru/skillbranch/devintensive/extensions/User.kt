package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.models.User
import ru.skillbranch.devintensive.models.UserView
import ru.skillbranch.devintensive.utils.Utils

fun User.toUserView():UserView{

    val nickName = Utils.transliteration("$firstName $lastName")
    val initiale = Utils.toInitials(firstName, lastName)
    val status = if (lastVisit == null) "Еще ни разу не был"
                 else if (isOnline) "online"
                 else "Последний раз был ${lastVisit?.humanizeDiff()}"

    return UserView(
            id,
            fullName = "$firstName $lastName",
            avatar = avatar,
            nickName = nickName ,
            status = status,
            initiale = initiale
    )
}
