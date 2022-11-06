package ie.setu.domain.db

/**
 * Database table object for sleep logs
 *
 * @author Michael Burke
 */

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one sleep log.
//       Database wise, this is the table object.

object Sleeps : Table("sleeps") {
    val id = integer("id").autoIncrement().primaryKey()
    val description = varchar("description", 100)
    val duration = double("duration")
    val rating = integer("rating")
    val wakeUpTime = datetime("wake_up_time")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}