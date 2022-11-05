package ie.setu.utils

val minAge =0
val maxAge = 140
val minHeight = 0.1
val maxHeight = 3.0
val minWeight = 5.0
val maxWeight = 1000.0

fun doubleIsValidInRange(minValue: Double, maxValue: Double, valueToCheck: Double): Boolean{
    return (valueToCheck >= minValue) && (valueToCheck <= maxValue)
}

fun intIsValidInRange(minValue: Int, maxValue: Int, valueToCheck: Int): Boolean{
    return (valueToCheck >= minValue) && (valueToCheck <= maxValue)
}