package ie.setu.utils

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.Water
import java.util.regex.Pattern

val minAge =0
val maxAge = 140
val minHeight = 0.1
val maxHeight = 3.0
val minWeight = 5.0
val maxWeight = 1000.0
val minVolume = 0.0
val maxVolume = 15.0
val validGenders = listOf('F', 'M', 'O')

fun isValidGender(genderToValidate: String): Boolean {
    return genderToValidate.get(0).uppercaseChar() in validGenders
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    return Pattern.matches(emailRegex, email)
}

fun doubleIsValidInRange(minValue: Double, maxValue: Double, valueToCheck: Double): Boolean{
    return (valueToCheck >= minValue) && (valueToCheck <= maxValue)
}

fun intIsValidInRange(minValue: Int, maxValue: Int, valueToCheck: Int): Boolean{
    return (valueToCheck >= minValue) && (valueToCheck <= maxValue)
}

fun waterInputValidation(water: Water): Boolean{
    return doubleIsValidInRange(minVolume, maxVolume,water.volume)
}

fun userInputValidation(user: User): Boolean{
    return intIsValidInRange(minAge, maxAge, user.age) &&
            doubleIsValidInRange(minWeight, maxWeight, user.weight) &&
            doubleIsValidInRange(minHeight, maxHeight, user.height) &&
            isValidGender(user.gender) &&
            isValidEmail(user.email)
}


