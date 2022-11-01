package ie.setu.Controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Sleep
import ie.setu.domain.User
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

//retrieving some test data from Fixtures
private val sleep1 = sleeps.get(0)
private val sleep2 = sleeps.get(1)
private val sleep3 = sleeps.get(2)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SleepControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class CreateSleeps {

        @Test
        fun `add an sleep when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated sleep that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addSleepResponse = addSleep(
                sleeps[0].description, sleeps[0].duration,
                sleeps[0].rating, sleeps[0].wakeUpTime, addedUser.id
            )
            assertEquals(201, addSleepResponse.status)

            //After - delete the user (Sleep will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add an sleep when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addSleepResponse = addSleep(
                sleeps.get(0).description, sleeps.get(0).duration,
                sleeps.get(0).rating, sleeps.get(0).wakeUpTime, userId
            )
            assertEquals(404, addSleepResponse.status)

        }
    }

    @Nested
    inner class ReadSleeps {

        @Test
        fun `get all sleeps from the database returns 200 or 404 response`() {
            val response = retrieveAllSleeps()
            if (response.status == 200){
                val retrievedSleeps = jsonNodeToObject<Array<Sleep>>(response)
                assertNotEquals(0, retrievedSleeps.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all sleeps by user id when user and sleeps exists returns 200 response`() {
            //Arrange - add a user and 3 associated sleeps that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            addSleep(
                sleeps[0].description, sleeps[0].duration,
                sleeps[0].rating, sleeps[0].wakeUpTime, addedUser.id)
            addSleep(
                sleeps[1].description, sleeps[1].duration,
                sleeps[1].rating, sleeps[1].wakeUpTime, addedUser.id)
            addSleep(
                sleeps[2].description, sleeps[2].duration,
                sleeps[2].rating, sleeps[2].wakeUpTime, addedUser.id)

            //Assert and Act - retrieve the three added sleeps by user id
            val response = retrieveSleepsByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedSleeps = jsonNodeToObject<Array<Sleep>>(response)
            assertEquals(3, retrievedSleeps.size)

            //After - delete the added user and assert a 204 is returned (sleeps are cascade deleted)
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all sleeps by user id when no sleeps exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the sleeps by user id
            val response = retrieveSleepsByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all sleeps by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve sleeps by user id
            val response = retrieveSleepsByUserId(userId)
            assertEquals(404, response.status)
        }

        @Test
        fun `get sleep by sleep id when no sleep exists returns 404 response`() {
            //Arrange
            val sleepId = -1
            //Assert and Act - attempt to retrieve the sleep by sleep id
            val response = retrieveSleepBySleepId(sleepId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get sleep by sleep id when sleep exists returns 200 response`() {
            //Arrange - add a user and associated sleep
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addSleepResponse = addSleep(
                sleeps[0].description,
                sleeps[0].duration, sleeps[0].rating,
                sleeps[0].wakeUpTime, addedUser.id)
            assertEquals(201, addSleepResponse.status)
            val addedSleep = jsonNodeToObject<Sleep>(addSleepResponse)

            //Act & Assert - retrieve the sleep by sleep id
            val response = retrieveSleepBySleepId(addedSleep.id)
            assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateSleeps {

        @Test
        fun `updating an sleep by sleep id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val sleepID = -1

            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveUserById(userId).status)

            //Act & Assert - attempt to update the details of an sleep/user that doesn't exist
            assertEquals(
                404, updateSleep(
                    sleepID, updatedDescription, updatedDuration,
                    updatedRating, updatedStarted, userId
                ).status
            )
        }

        @Test
        fun `updating an sleep by sleep id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated sleep that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addSleepResponse = addSleep(
                sleeps[0].description,
                sleeps[0].duration, sleeps[0].rating,
                sleeps[0].wakeUpTime, addedUser.id)
            assertEquals(201, addSleepResponse.status)
            val addedSleep = jsonNodeToObject<Sleep>(addSleepResponse)

            //Act & Assert - update the added sleep and assert a 204 is returned
            val updatedSleepResponse = updateSleep(addedSleep.id, updatedDescription,
                updatedDuration, updatedRating, updatedStarted, addedUser.id)
            assertEquals(204, updatedSleepResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedSleepResponse = retrieveSleepBySleepId(addedSleep.id)
            val updatedSleep = jsonNodeToObject<Sleep>(retrievedSleepResponse)
            assertEquals(updatedDescription,updatedSleep.description)
            assertEquals(updatedDuration, updatedSleep.duration, 0.1)
            assertEquals(updatedRating, updatedSleep.rating)
            assertEquals(updatedStarted, updatedSleep.wakeUpTime )

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }

    @Nested
    inner class DeleteSleeps {

        @Test
        fun `deleting an sleep by sleep id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a sleep  that doesn't exist
            assertEquals(404, deleteSleepBySleepId(-1).status)
        }

        @Test
        fun `deleting sleeps by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete sleeps by a user that that doesn't exist
            assertEquals(404, deleteSleepsByUserId(-1).status)
        }

        @Test
        fun `deleting an sleep by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated sleep that we plan to do a delete on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addSleepResponse = addSleep(
                sleeps[0].description, sleeps[0].duration,
                sleeps[0].rating, sleeps[0].wakeUpTime, addedUser.id)
            assertEquals(201, addSleepResponse.status)

            //Act & Assert - delete the added sleep and assert a 204 is returned
            val addedSleep = jsonNodeToObject<Sleep>(addSleepResponse)
            assertEquals(204, deleteSleepBySleepId(addedSleep.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all sleeps by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated sleeps that we plan to do a cascade delete
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addSleepResponse1 = addSleep(
                sleeps[0].description, sleeps[0].duration,
                sleeps[0].rating, sleeps[0].wakeUpTime, addedUser.id)
            assertEquals(201, addSleepResponse1.status)
            val addSleepResponse2 = addSleep(
                sleeps[1].description, sleeps[1].duration,
                sleeps[1].rating, sleeps[1].wakeUpTime, addedUser.id)
            assertEquals(201, addSleepResponse2.status)
            val addSleepResponse3 = addSleep(
                sleeps[2].description, sleeps[2].duration,
                sleeps[2].rating, sleeps[2].wakeUpTime, addedUser.id)
            assertEquals(201, addSleepResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted sleeps
            val addedSleep1 = jsonNodeToObject<Sleep>(addSleepResponse1)
            val addedSleep2 = jsonNodeToObject<Sleep>(addSleepResponse2)
            val addedSleep3 = jsonNodeToObject<Sleep>(addSleepResponse3)
            assertEquals(404, retrieveSleepBySleepId(addedSleep1.id).status)
            assertEquals(404, retrieveSleepBySleepId(addedSleep2.id).status)
            assertEquals(404, retrieveSleepBySleepId(addedSleep3.id).status)
        }
    }
    
    //helper function to retrieve all sleeps
    private fun retrieveAllSleeps(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/sleeps").asJson()
    }

    //helper function to retrieve sleeps by user id
    private fun retrieveSleepsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/sleeps").asJson()
    }

    //helper function to retrieve sleep by sleep id
    private fun retrieveSleepBySleepId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/sleeps/${id}").asJson()
    }

    //helper function to delete an sleep by sleep id
    private fun deleteSleepBySleepId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/sleeps/$id").asString()
    }

    //helper function to delete an sleep by sleep id
    private fun deleteSleepsByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/sleeps").asString()
    }

    //helper function to add a test user to the database
    private fun updateSleep(id: Int, description: String, duration: Double, rating: Int,
                            wakeUpTime: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/sleeps/$id")
            .body("""
                {
                  "description":"$description",
                  "duration":$duration,
                  "rating":$rating,
                  "wakeUpTime":"$wakeUpTime",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }

    //helper function to add an sleep
    private fun addSleep(description: String, duration: Double, rating: Int,
                            wakeUpTime: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/sleeps")
            .body("""
                {
                   "description":"$description",
                   "duration":$duration,
                   "rating":$rating,
                   "wakeUpTime":"$wakeUpTime",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }
}