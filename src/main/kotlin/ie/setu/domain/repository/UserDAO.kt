package ie.setu.domain.repository

/**
 * Manages the database user transactions and returns the results of the transactions
 */

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserDAO {

    // get all users
    /**
     * Find all the [User] in the Users table
     * @return array list of Users
     */
    fun getAll(): ArrayList<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it)) }
        }
        return userList
    }

    /**
     * Find a [User] in the Users table by ID
     * @return the User
     */
    fun findById(id: Int): User?{
        return transaction {
            Users.select() {
                Users.id eq id}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    /**
     * Adds a [User] to the Users table
     * @return the id of the user following the add
     */
    fun save(user: User) : Int?{
        return transaction {
            Users.insert {
                it[name] = user.name
                it[email] = user.email
                it[weight] = user.weight
                it[height] = user.height
                it[age] = user.age
                it[gender] = user.gender
            } get Users.id
        }
    }

    /**
     * Find a [User] from the Users table by email
     * @return the user
     */
    fun findByEmail(email: String) :User?{
        return transaction {
            Users.select() {
                Users.email eq email}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    /**
     * Delete a [User] from the Users table
     * @return the transaction status
     */
    fun delete(id: Int):Int{
        return transaction{
            Users.deleteWhere{
                Users.id eq id
            }
        }
    }

    /**
     * Update a [User] in the Users table
     * @return the transaction status
     */
    fun update(id: Int, user: User): Int{
        return transaction {
            Users.update ({
                Users.id eq id}) {
                it[name] = user.name
                it[email] = user.email
                it[weight] = user.weight
                it[height] = user.height
                it[age] = user.age
                it[gender] = user.gender
            }
        }
    }
}