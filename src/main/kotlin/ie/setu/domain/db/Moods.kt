package ie.setu.domain.db

/**
 * Database table object for mood logs
 *
 * @author Michael Burke
 */

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one mood.
//       Database wise, this is the table object.

object Moods : Table("moods") {
    val id = integer("id").autoIncrement().primaryKey()
    val description = varchar("description", 100)
    val rating = integer("rating")
    val dateEntry = datetime("date_entry")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}