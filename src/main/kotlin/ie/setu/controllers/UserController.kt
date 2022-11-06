package ie.setu.controllers

/**
 * Controller object for User
 *
 * @author Michael Burke
 */

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User
import ie.setu.domain.Water
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonToObject
import ie.setu.utils.userInputValidation
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object UserController {

    private val userDao = UserDAO()

    @OpenApi(
        summary = "Get all users",
        operationId = "getAllUsers",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)]),
            OpenApiResponse("404", [OpenApiContent(Array<User>::class)])]
    )
    fun getAllUsers(ctx: Context) {
        val users = userDao.getAll()
        if (users.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(users)
    }

    @OpenApi(
        summary = "Get user by ID",
        operationId = "getUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)]),
            OpenApiResponse("404", [OpenApiContent(Array<User>::class)])]
    )
    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add User",
        operationId = "addUser",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.POST,
        responses  = [OpenApiResponse("201", [OpenApiContent(User::class)]),
            OpenApiResponse("400") ]
    )
    fun addUser(ctx: Context) {
        val user : User = jsonToObject(ctx.body())

        if (!userInputValidation(user)){
            ctx.status(400)
        }
        else {
            val userId = userDao.save(user)
            if (userId != null) {
                user.id = userId
                ctx.json(user)
                ctx.status(201)
            }
        }

    }

    @OpenApi(
        summary = "Get user by Email",
        operationId = "getUserByEmail",
        tags = ["User"],
        path = "/api/users/email/{email}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("email", String::class, "The user email")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)]), OpenApiResponse("404")]
    )
    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete user by ID",
        operationId = "deleteUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("404")]
    )
    fun deleteUser(ctx: Context){
        if (userDao.delete(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update user by ID",
        operationId = "updateUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("400"), OpenApiResponse("404")]
    )
    fun updateUser(ctx: Context){
        val foundUser : User = jsonToObject(ctx.body())
        if (!userInputValidation(foundUser)){
            ctx.status(400)
        }
        else {
            if ((userDao.update(id = ctx.pathParam("user-id").toInt(), user = foundUser)) != 0)
                ctx.status(204)
            else
                ctx.status(404)
        }
    }

}