package ie.setu.repository

import ie.setu.domain.Activity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.domain.db.Sleeps
import ie.setu.domain.Sleep
import ie.setu.domain.db.Activities
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.SleepDAO
import ie.setu.helpers.*
import org.junit.jupiter.api.Disabled
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val sleep1 = sleeps.get(0)
private val sleep2 = sleeps.get(1)
private val sleep3 = sleeps.get(2)

class SleepDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateSleeps {

        @Test
        fun `multiple sleep logs added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleep logs
                val userDAO = populateUserTable()
                val sleepDAO = populateSleepTable()

                //Act & Assert
                assertEquals(3, sleepDAO.getAll().size)
                assertEquals(sleep1, sleepDAO.findBySleepId(sleep1.id))
                assertEquals(sleep2, sleepDAO.findBySleepId(sleep2.id))
                assertEquals(sleep3, sleepDAO.findBySleepId(sleep3.id))
            }
        }
    }

    @Nested
    inner class ReadSleeps {

        @Test
        fun `getting all activites from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleeps
                val userDAO = populateUserTable()
                val sleepDAO = populateSleepTable()
                //Act & Assert
                assertEquals(3, sleepDAO.getAll().size)
            }
        }

        @Test
        fun `get sleep by user id that has no sleeps, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleeps
                val userDAO = populateUserTable()
                val sleepDAO = populateSleepTable()
                //Act & Assert
                assertEquals(0, sleepDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get sleep by user id that exists, results in a correct activitie(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleeps
                val userDAO = populateUserTable()
                val sleepDAO = populateSleepTable()
                //Act & Assert
                assertEquals(sleep1, sleepDAO.findByUserId(1).get(0))
                assertEquals(sleep2, sleepDAO.findByUserId(1).get(1))
                assertEquals(sleep3, sleepDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all sleeps over empty table returns none`() {
            transaction {

                //Arrange - create and setup sleepDAO object
                SchemaUtils.create(Sleeps)
                val sleepDAO = SleepDAO()

                //Act & Assert
                assertEquals(0, sleepDAO.getAll().size)
            }
        }

        @Test
        fun `get sleep by sleep id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleeps
                val userDAO = populateUserTable()
                val sleepDAO = populateSleepTable()
                //Act & Assert
                assertEquals(null, sleepDAO.findBySleepId(4))
            }
        }

        @Test
        fun `get sleep by sleep id that exists, results in a correct sleep returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three sleeps
                val userDAO = populateUserTable()
                val sleepDAO = populateSleepTable()
                //Act & Assert
                assertEquals(sleep1, sleepDAO.findBySleepId(1))
                assertEquals(sleep3, sleepDAO.findBySleepId(3))
            }
        }
    }

    @Nested
    inner class UpdateSleeps {

        @Test
        fun `updating existing sleep in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three sleeps
                val userDAO = populateUserTable()
                val sleepDAO = populateSleepTable()

                //Act & Assert
                val sleep3updated = Sleep(id = 3, description = "Ok", duration = 6.5,
                    rating = 6, wakeUpTime = DateTime.now(), userId = 2)
                sleepDAO.updateBySleepId(sleep3updated.id, sleep3updated)
                assertEquals(sleep3updated, sleepDAO.findBySleepId(3))
            }
        }

        @Test
        fun `updating non-existant sleep in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three sleeps
                val userDAO = populateUserTable()
                val sleepDAO = populateSleepTable()

                //Act & Assert
                val sleep4updated = Sleep(id = 4, description = "awful", duration = 3.5,
                    rating = 1, wakeUpTime = DateTime.now(), userId = 2)
                sleepDAO.updateBySleepId(4, sleep4updated)
                assertEquals(null, sleepDAO.findBySleepId(4))
                assertEquals(3, sleepDAO.getAll().size)
            }
        }
    }

}