package ie.setu.repository

import ie.setu.helpers.populateWaterTable
import ie.setu.helpers.populateUserTable
import ie.setu.helpers.waters
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val water1 = waters.get(0)
private val water2 = waters.get(1)
private val water3 = waters.get(2)

class WaterDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateWaters {

        @Test
        fun `multiple water logs added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three water logs
                val userDAO = populateUserTable()
                val waterDAO = populateWaterTable()

                //Act & Assert
                assertEquals(3, waterDAO.getAll().size)
                assertEquals(water1, waterDAO.findByWaterId(water1.id))
                assertEquals(water2, waterDAO.findByWaterId(water2.id))
                assertEquals(water3, waterDAO.findByWaterId(water3.id))
            }
        }
    }
    
}