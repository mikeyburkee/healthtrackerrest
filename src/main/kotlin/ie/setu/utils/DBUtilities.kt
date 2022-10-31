package ie.setu.utils

import org.jetbrains.exposed.sql.ResultRow
import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.domain.Mood
import ie.setu.domain.db.Moods

fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email]
)

fun mapToActivity(it: ResultRow) = Activity(
    id = it[Activities.id],
    description = it[Activities.description],
    duration = it[Activities.duration],
    started = it[Activities.started],
    calories = it[Activities.calories],
    userId = it[Activities.userId]
)

fun mapToMood(it: ResultRow) = Mood(
    id = it[Moods.id],
    description = it[Moods.description],
    rating = it[Moods.rating],
    dateEntry = it[Moods.dateEntry],
    userId = it[Moods.userId]
)