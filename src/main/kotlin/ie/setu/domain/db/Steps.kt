package ie.setu.domain.db

/**
 * Database table object for steps logs
 *
 * @author Michael Burke
 */

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one steps log.
//       Database wise, this is the table object.

object Steps : Table("steps") {
    val id = integer("id").autoIncrement().primaryKey()
    val step_count = integer("step_count")
    val dateEntry = datetime("dateentry")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}