package ie.setu.utils

import ie.setu.domain.*
import org.jetbrains.exposed.sql.ResultRow
import ie.setu.domain.db.*

fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email],
    weight = it[Users.weight],
    height = it[Users.height],
    age = it[Users.age],
    gender = it[Users.gender],
)

fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId],
    rating = it[Activities.rating]
)

fun mapToMood(it: ResultRow) = Mood(
    id = it[Moods.id],
    description = it[Moods.description],
    rating = it[Moods.rating],
    dateEntry = it[Moods.dateEntry],
    userId = it[Moods.userId]
)

fun mapToSleep(it: ResultRow) = Sleep(
    id = it[Sleeps.id],
    description = it[Sleeps.description],
    duration = it[Sleeps.duration],
    wakeUpTime = it[Sleeps.wakeUpTime],
    rating = it[Sleeps.rating],
    userId = it[Sleeps.userId]
)

fun mapToWater(it: ResultRow) = Water(
    id = it[Waters.id],
    volume = it[Waters.volume],
    dateEntry = it[Waters.dateEntry],
    userId = it[Waters.userId]
)