package ie.setu.utils

/**
 * Input Validation utilities file
 *
 * @author Michael Burke
 */

import ie.setu.domain.*
import java.util.regex.Pattern

val minAge =0
val maxAge = 140
val minHeight = 0.1
val maxHeight = 3.0
val minWeight = 5.0
val maxWeight = 1000.0
val validGenders = listOf('F', 'M', 'O')

val minVolume = 0.0
val maxVolume = 15.0

val minActivityDuration = 0.0
val maxActivityDuration = 4320.0                        // 3 days longest duration extreme endurance
val minActivityCalories = 0
val maxActivityCalories = 50000                      // 50,000 calories based on research

val minRating = 0
val maxRating = 10

val minSleepDuration = 0.0
val maxSleepDuration = 24.0

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

fun activityInputValidation(activity: Activity): Boolean{
    return intIsValidInRange(minActivityCalories, maxActivityCalories, activity.calories) &&
            doubleIsValidInRange(minActivityDuration, maxActivityDuration, activity.duration) &&
            intIsValidInRange(minRating, maxRating, activity.rating)
}

fun moodInputValidation(mood: Mood): Boolean{
    return intIsValidInRange(minRating, maxRating, mood.rating)
}

fun sleepInputValidation(sleep: Sleep): Boolean{
    return doubleIsValidInRange(minSleepDuration, maxSleepDuration, sleep.duration) &&
            intIsValidInRange(minRating, maxRating, sleep.rating)
}