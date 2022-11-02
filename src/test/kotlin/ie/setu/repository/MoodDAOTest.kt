package ie.setu.repository

import ie.setu.domain.Activity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.domain.db.Moods
import ie.setu.domain.Mood
import ie.setu.domain.repository.MoodDAO
import ie.setu.helpers.*
import org.junit.jupiter.api.Disabled
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val mood1 = moods.get(0)
private val mood2 = moods.get(1)
private val mood3 = moods.get(2)

class MoodDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateMoods {

        @Test
        fun `multiple moods added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateMoodTable()

                //Act & Assert
                assertEquals(3, moodDAO.getAll().size)
                assertEquals(mood1, moodDAO.findByMoodId(mood1.id))
                assertEquals(mood2, moodDAO.findByMoodId(mood2.id))
                assertEquals(mood3, moodDAO.findByMoodId(mood3.id))
            }
        }
    }

    @Nested
    inner class ReadMoods {

        @Test
        fun `getting all moods from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateMoodTable()
                //Act & Assert
                assertEquals(3, moodDAO.getAll().size)
            }
        }

        @Test
        fun `get mood by user id that has no moods, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateActivityTable()
                //Act & Assert
                assertEquals(0, moodDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get mood by user id that exists, results in a correct mood(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateMoodTable()
                //Act & Assert
                assertEquals(mood1, moodDAO.findByUserId(1).get(0))
                assertEquals(mood2, moodDAO.findByUserId(1).get(1))
                assertEquals(mood3, moodDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all moods over empty table returns none`() {
            transaction {

                //Arrange - create and setup moodDAO object
                SchemaUtils.create(Moods)
                val moodDAO = MoodDAO()

                //Act & Assert
                assertEquals(0, moodDAO.getAll().size)
            }
        }

        @Test
        fun `get mood by mood id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateMoodTable()
                //Act & Assert
                assertEquals(null, moodDAO.findByMoodId(4))
            }
        }

        @Test
        fun `get mood by mood id that exists, results in a correct mood returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateMoodTable()
                //Act & Assert
                assertEquals(mood1, moodDAO.findByMoodId(1))
                assertEquals(mood3, moodDAO.findByMoodId(3))
            }
        }
    }

    @Nested
    inner class UpdateMoods {

        @Test
        fun `updating existing mood in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateMoodTable()

                //Act & Assert
                val mood3updated = Mood(id = 3, description = "Very bad", rating = 1,
                    dateEntry = DateTime.now(), userId = 2)
                moodDAO.updateByMoodId(mood3updated.id, mood3updated)
                assertEquals(mood3updated, moodDAO.findByMoodId(3))
            }
        }

        @Test
        fun `updating non-existent mood in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateMoodTable()

                //Act & Assert
                val mood4updated = Mood(id = 4, description = "happy", rating = 7,
                    dateEntry = DateTime.now(), userId = 2)
                moodDAO.updateByMoodId(4, mood4updated)
                assertEquals(null, moodDAO.findByMoodId(4))
                assertEquals(3, moodDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteMoods {

        @Test
        fun `deleting a non-existent mood (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three moods
                populateUserTable()
                val moodDAO = populateMoodTable()

                //Act & Assert
                assertEquals(3, moodDAO.getAll().size)
                moodDAO.deleteByMoodId(4)
                assertEquals(3, moodDAO.getAll().size)


            }
        }

        @Test
        fun `deleting an existing mood (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three activities
                populateUserTable()
                val moodDAO = populateMoodTable()

                //Act & Assert
                assertEquals(3, moodDAO.getAll().size)
                moodDAO.deleteByMoodId(mood3.id)
                assertEquals(2, moodDAO.getAll().size)
            }
        }

        @Test
        fun `deleting moods when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three moods
                populateUserTable()
                val moodDAO = populateMoodTable()

                //Act & Assert
                assertEquals(3, moodDAO.getAll().size)
                moodDAO.deleteByUserId(3)
                assertEquals(3, moodDAO.getAll().size)
            }
        }

        @Test
        fun `deleting moods when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three moods
                populateUserTable()
                val moodDAO = populateMoodTable()

                //Act & Assert
                assertEquals(3, moodDAO.getAll().size)
                moodDAO.deleteByUserId(1)
                assertEquals(1, moodDAO.getAll().size)
            }
        }
    }
}