package ie.setu.Controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Activity
import ie.setu.domain.Mood
import ie.setu.domain.User
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

//retrieving some test data from Fixtures
private val activity1 = moods.get(0)
private val activity2 = moods.get(1)
private val activity3 = moods.get(2)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoodControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class CreateMoods {

        @Test
        fun `add an activity when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated mood that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addMoodResponse = addMood(
                moods[0].description, moods[0].rating,
                moods[0].dateEntry, addedUser.id
            )
            assertEquals(201, addMoodResponse.status)

            //After - delete the user (Mood will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add a mood when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addMoodResponse = addMood(
                moods.get(0).description, moods.get(0).rating,
                moods.get(0).dateEntry, userId
            )
            assertEquals(404, addMoodResponse.status)
        }
    }

    @Nested
    inner class ReadMoods {

        @Test
        fun `get all moods from the database returns 200 or 404 response`() {
            val response = retrieveAllMoods()
            if (response.status == 200){
                val retrievedMoods = jsonNodeToObject<Array<Mood>>(response)
                assertNotEquals(0, retrievedMoods.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all moods by user id when user and moods exists returns 200 response`() {
            //Arrange - add a user and 3 associated moods that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            addMood(
                moods[0].description, moods[0].rating,
                moods[0].dateEntry, addedUser.id)
            addMood(
                moods[1].description, moods[1].rating,
                moods[1].dateEntry, addedUser.id)
            addMood(
                moods[2].description, moods[2].rating,
                moods[2].dateEntry, addedUser.id)

            //Assert and Act - retrieve the three added moods by user id
            val response = retrieveMoodsByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedMoods = jsonNodeToObject<Array<Mood>>(response)
            assertEquals(3, retrievedMoods.size)

            //After - delete the added user and assert a 204 is returned (moods are cascade deleted)
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all moods by user id when no moods exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the moods by user id
            val response = retrieveMoodsByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all moods by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve moods by user id
            val response = retrieveMoodsByUserId(userId)
            assertEquals(404, response.status)
        }

        @Test
        fun `get mood by mood id when no mood exists returns 404 response`() {
            //Arrange
            val moodId = -1
            //Assert and Act - attempt to retrieve the moods by moods id
            val response = retrieveMoodByMoodId(moodId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get mood by mood id when mood exists returns 200 response`() {
            //Arrange - add a user and associated mood
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMoodResponse = addMood(
                moods[0].description,
                moods[0].rating,
                moods[0].dateEntry, addedUser.id)
            assertEquals(201, addMoodResponse.status)
            val addedMood = jsonNodeToObject<Mood>(addMoodResponse)

            //Act & Assert - retrieve the mood by mood id
            val response = retrieveMoodByMoodId(addedMood.id)
            assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateMoods {

        @Test
        fun `updating an mood by mood id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val moodID = -1

            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveUserById(userId).status)

            //Act & Assert - attempt to update the details of an activity/user that doesn't exist
            assertEquals(
                404, updateMood(
                    moodID, updatedDescription, updatedRating,
                    updatedDateEntry, userId
                ).status
            )
        }

        @Test
        fun `updating a mood by mood id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated activity that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMoodResponse = addMood(
                moods[0].description,
                moods[0].rating,
                moods[0].dateEntry, addedUser.id)
            assertEquals(201, addMoodResponse.status)
            val addedMood = jsonNodeToObject<Mood>(addMoodResponse)

            //Act & Assert - update the added activity and assert a 204 is returned
            val updatedMoodResponse = updateMood(addedMood.id, updatedDescription,
                updatedRating, updatedDateEntry, addedUser.id)
            assertEquals(204, updatedMoodResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedMoodResponse = retrieveMoodByMoodId(addedMood.id)
            val updatedMood = jsonNodeToObject<Mood>(retrievedMoodResponse)
            assertEquals(updatedDescription,updatedMood.description)
            assertEquals(updatedRating, updatedMood.rating)
            assertEquals(updatedDateEntry, updatedMood.dateEntry )

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }

    //helper function to retrieve all moods
    private fun retrieveAllMoods(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/moods").asJson()
    }

    //helper function to retrieve moods by user id
    private fun retrieveMoodsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/moods").asJson()
    }

    //helper function to retrieve mood by mood id
    private fun retrieveMoodByMoodId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/moods/${id}").asJson()
    }

    //helper function to delete a mood by mood id
    private fun deleteMoodByMoodId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/moods/$id").asString()
    }

    //helper function to delete a mood by mood id
    private fun deleteMoodsByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/moods").asString()
    }

    //helper function to add a test user to the database
    private fun updateMood(id: Int, description: String, rating: Int,
                           dateEntry: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/moods/$id")
            .body("""
                {
                  "description":"$description",
                  "rating":$rating,
                  "dateEntry":"$dateEntry",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }

    //helper function to add a mood
    private fun addMood(description: String, rating: Int,
                            dateEntry: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/moods")
            .body("""
                {
                   "description":"$description",
                   "rating":$rating,
                   "dateEntry":"$dateEntry",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }
}