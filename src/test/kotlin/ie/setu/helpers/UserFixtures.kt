package ie.setu.helpers

/**
 * User Fixtures file used in controller test classes
 *
 * @author Michael Burke
 */

import ie.setu.config.DbConfig
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest

val validName = "Test User 1"
val validEmail = "testuser1@test.com"
val validWeight = 80.5
val validHeight = 1.7
val validAge = 25
val validGender = "male"

val db = DbConfig().getDbConnection()
val app = ServerContainer.instance
val origin = "http://localhost:" + app.port()

//helper function to add a test user to the database
fun addUser (name: String, email: String, weight: Double, height: Double, age: Int, gender: String): HttpResponse<JsonNode> {
    return Unirest.post(origin + "/api/users")
        .body("{\"name\":\"$name\", " +
                "\"email\":\"$email\", " +
                "\"weight\":\"$weight\", " +
                "\"height\":\"$height\", " +
                "\"age\":\"$age\", " +
                "\"gender\":\"$gender\" " +
                "}")
        .asJson()
}

//helper function to delete a test user from the database
fun deleteUser (id: Int): HttpResponse<String> {
    return Unirest.delete(origin + "/api/users/$id").asString()
}

//helper function to retrieve a test user from the database by email
fun retrieveUserByEmail(email : String) : HttpResponse<String> {
    return Unirest.get(origin + "/api/users/email/${email}").asString()
}

//helper function to retrieve a test user from the database by id
fun retrieveUserById(id: Int) : HttpResponse<String> {
    return Unirest.get(origin + "/api/users/${id}").asString()
}

//helper function to add a test user to the database
fun updateUser (id: Int, name: String, email: String, weight: Double, height: Double, age: Int, gender: String): HttpResponse<JsonNode> {
    return Unirest.patch(origin + "/api/users/$id")
        .body("{\"name\":\"$name\", " +
                "\"email\":\"$email\", " +
                "\"weight\":\"$weight\", " +
                "\"height\":\"$height\", " +
                "\"age\":\"$age\", " +
                "\"gender\":\"$gender\" " +
                "}")
        .asJson()
}