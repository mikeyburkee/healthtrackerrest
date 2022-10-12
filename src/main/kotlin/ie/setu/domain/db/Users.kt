package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

// SRP - Responsibility is to manage one user.
//       Database wise, this is the table object.

object Users : Table("users") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 100)
    val email = varchar("email", 255)
}