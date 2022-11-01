package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one water log.
//       Database wise, this is the table object.

object Waters : Table("activities") {
    val id = integer("id").autoIncrement().primaryKey()
    val volume = double("volume")
    val dateEntry = datetime("date_entry")
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
}