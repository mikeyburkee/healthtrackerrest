package ie.setu.Controllers

import ie.setu.config.DbConfig
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

//retrieving some test data from Fixtures
private val mood1 = moods[0]
private val mood2 = moods[1]
private val mood3 = moods[2]

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoodControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class CreateMoods {

        @Test
        fun `add an mood when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated mood that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addMoodResponse = addMood(
                mood1.description, mood1.rating,
                mood1.dateEntry, addedUser.id
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
                mood1.description, mood1.rating,
                mood1.dateEntry, addedUser.id)
            addMood(
                mood2.description, mood2.rating,
                mood2.dateEntry, addedUser.id)
            addMood(
                mood3.description, mood3.rating,
                mood3.dateEntry, addedUser.id)

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
                mood1.description,
                mood1.rating,
                mood1.dateEntry, addedUser.id)
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

            //Act & Assert - attempt to update the details of an mood/user that doesn't exist
            assertEquals(
                404, updateMood(
                    moodID, updatedDescription, updatedRating,
                    updatedDateTime, userId
                ).status
            )
        }

        @Test
        fun `updating a mood by mood id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated mood that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMoodResponse = addMood(
                mood1.description,
                mood1.rating,
                mood1.dateEntry, addedUser.id)
            assertEquals(201, addMoodResponse.status)
            val addedMood = jsonNodeToObject<Mood>(addMoodResponse)

            //Act & Assert - update the added mood and assert a 204 is returned
            val updatedMoodResponse = updateMood(addedMood.id, updatedDescription,
                updatedRating, updatedDateTime, addedUser.id)
            assertEquals(204, updatedMoodResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedMoodResponse = retrieveMoodByMoodId(addedMood.id)
            val updatedMood = jsonNodeToObject<Mood>(retrievedMoodResponse)
            assertEquals(updatedDescription,updatedMood.description)
            assertEquals(updatedRating, updatedMood.rating)
            assertEquals(updatedDateTime, updatedMood.dateEntry )

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }

    @Nested
    inner class DeleteMoods {

        @Test
        fun `deleting a mood by mood id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a mood that doesn't exist
            assertEquals(404, deleteMoodByMoodId(-1).status)
        }

        @Test
        fun `deleting moods by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete moods by user that doesn't exist
            assertEquals(404, deleteMoodsByUserId(-1).status)
        }

        @Test
        fun `deleting a mood by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated mood that we plan to do a delete on
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMoodResponse = addMood(
                mood1.description, mood1.rating,
                mood1.dateEntry, addedUser.id)
            assertEquals(201, addMoodResponse.status)

            //Act & Assert - delete the added mood and assert a 204 is returned
            val addedMood = jsonNodeToObject<Mood>(addMoodResponse)
            assertEquals(204, deleteMoodByMoodId(addedMood.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all moods by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated moods that we plan to do a cascade delete
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addMoodResponse1 = addMood(
                mood1.description, mood1.rating,
                mood1.dateEntry, addedUser.id)
            assertEquals(201, addMoodResponse1.status)
            val addMoodResponse2 = addMood(
                mood2.description, mood2.rating,
                mood2.dateEntry, addedUser.id)
            assertEquals(201, addMoodResponse2.status)
            val addMoodResponse3 = addMood(
                mood3.description, mood3.rating,
                mood3.dateEntry, addedUser.id)
            assertEquals(201, addMoodResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted moods
            val addedMood1 = jsonNodeToObject<Mood>(addMoodResponse1)
            val addedMood2 = jsonNodeToObject<Mood>(addMoodResponse2)
            val addedMood3 = jsonNodeToObject<Mood>(addMoodResponse3)
            assertEquals(404, retrieveMoodByMoodId(addedMood1.id).status)
            assertEquals(404, retrieveMoodByMoodId(addedMood2.id).status)
            assertEquals(404, retrieveMoodByMoodId(addedMood3.id).status)
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