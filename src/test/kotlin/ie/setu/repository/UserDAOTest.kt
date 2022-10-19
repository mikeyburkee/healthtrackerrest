package ie.setu.repository

import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeAll

//retrieving some test data from Fixtures
val user1 = users.get(0)
val user2 = users.get(1)
val user3 = users.get(2)

class UserDAOTest {

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

}