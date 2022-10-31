package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one mood.
//       Database wise, this is the table object.

object Moods : Table("moods") {
    val id = integer("id").autoIncrement().primaryKey()
    val description = varchar("description", 100)
    val mood_value = integer("mood_value")
    val logged = datetime("started")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}