package ie.setu.repository

/**
 * DAO unit test class for waters
 *
 * @author Michael Burke
 */

import ie.setu.domain.Water
import ie.setu.domain.db.Waters
import ie.setu.domain.repository.WaterDAO
import ie.setu.helpers.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
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
                populateUserTable()
                val waterDAO = populateWaterTable()

                //Act & Assert
                assertEquals(3, waterDAO.getAll().size)
                assertEquals(water1, waterDAO.findByWaterId(water1.id))
                assertEquals(water2, waterDAO.findByWaterId(water2.id))
                assertEquals(water3, waterDAO.findByWaterId(water3.id))
            }
        }
    }

    @Nested
    inner class ReadWaters {

        @Test
        fun `getting all activites from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()
                //Act & Assert
                assertEquals(3, waterDAO.getAll().size)
            }
        }

        @Test
        fun `get water by user id that has no waters, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()
                //Act & Assert
                assertEquals(0, waterDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get water by user id that exists, results in a correct activitie(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()
                //Act & Assert
                assertEquals(water1, waterDAO.findByUserId(1).get(0))
                assertEquals(water2, waterDAO.findByUserId(1).get(1))
                assertEquals(water3, waterDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all waters over empty table returns none`() {
            transaction {

                //Arrange - create and setup waterDAO object
                SchemaUtils.create(Waters)
                val waterDAO = WaterDAO()

                //Act & Assert
                assertEquals(0, waterDAO.getAll().size)
            }
        }

        @Test
        fun `get water by water id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()
                //Act & Assert
                assertEquals(null, waterDAO.findByWaterId(4))
            }
        }

        @Test
        fun `get water by water id that exists, results in a correct water returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()
                //Act & Assert
                assertEquals(water1, waterDAO.findByWaterId(1))
                assertEquals(water3, waterDAO.findByWaterId(3))
            }
        }
    }

    @Nested
    inner class UpdateWaters {

        @Test
        fun `updating existing water in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()

                //Act & Assert
                val water3updated = Water(id = 3, volume = 1.1,
                    dateEntry = DateTime.now(), userId = 2)
                waterDAO.updateByWaterId(water3updated.id, water3updated)
                assertEquals(water3updated, waterDAO.findByWaterId(3))
            }
        }

        @Test
        fun `updating non-existent water in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()

                //Act & Assert
                val water4updated = Water(id = 4, volume = 1.4,
                    dateEntry = DateTime.now(), userId = 2)
                waterDAO.updateByWaterId(4, water4updated)
                assertEquals(null, waterDAO.findByWaterId(4))
                assertEquals(3, waterDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteWaters {

        @Test
        fun `deleting a non-existent water (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()

                //Act & Assert
                assertEquals(3, waterDAO.getAll().size)
                waterDAO.deleteByWaterId(4)
                assertEquals(3, waterDAO.getAll().size)


            }
        }

        @Test
        fun `deleting an existing water (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()

                //Act & Assert
                assertEquals(3, waterDAO.getAll().size)
                waterDAO.deleteByWaterId(water3.id)
                assertEquals(2, waterDAO.getAll().size)
            }
        }


        @Test
        fun `deleting waters when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()

                //Act & Assert
                assertEquals(3, waterDAO.getAll().size)
                waterDAO.deleteByUserId(3)
                assertEquals(3, waterDAO.getAll().size)
            }
        }

        @Test
        fun `deleting waters when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three waters
                populateUserTable()
                val waterDAO = populateWaterTable()

                //Act & Assert
                assertEquals(3, waterDAO.getAll().size)
                waterDAO.deleteByUserId(1)
                assertEquals(1, waterDAO.getAll().size)
            }
        }
    }
    
}