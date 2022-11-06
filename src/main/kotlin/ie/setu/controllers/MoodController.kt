package ie.setu.controllers

/**
 * Controller object for mood logs
 *
 * @author Michael Burke
 */

import ie.setu.domain.Mood
import ie.setu.domain.Water
import ie.setu.domain.repository.MoodDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonToObject
import ie.setu.utils.moodInputValidation
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object MoodController {

    private val userDao = UserDAO()
    private val moodDAO = MoodDAO()

    @OpenApi(
        summary = "Get all moods",
        operationId = "getAllMoods",
        tags = ["Mood"],
        path = "/api/moods",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Mood>::class)]),
            OpenApiResponse("404", [OpenApiContent(Array<Mood>::class)]) ]
    )
    fun getAllMoods(ctx: Context) {
        val moods = moodDAO.getAll()
        if (moods.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(moods)
    }

    @OpenApi(
        summary = "Get moods by user ID",
        operationId = "getMoodsByUserId",
        tags = ["Mood"],
        path = "/api/users/{user-id}/moods",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Mood::class)]),
            OpenApiResponse("404")]
    )
    fun getMoodsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val moods = moodDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (moods.isNotEmpty()) {
                ctx.json(moods)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add Mood",
        operationId = "addMood",
        tags = ["Mood"],
        path = "/api/moods",
        method = HttpMethod.POST,
        responses  = [OpenApiResponse("201", [OpenApiContent(Mood::class)]),
            OpenApiResponse("404"),OpenApiResponse("400") ]
    )
    fun addMood(ctx: Context) {
        val mood : Mood = jsonToObject(ctx.body())
        val userId = userDao.findById(mood.userId)
        if (!moodInputValidation(mood)){
            ctx.status(400)
        }
        else {
            if (userId != null) {
                val moodId = moodDAO.save(mood)
                mood.id = moodId
                ctx.json(mood)
                ctx.status(201)
            } else {
                ctx.status(404)
            }
        }
    }

    @OpenApi(
        summary = "Get mood by mood ID",
        operationId = "getMoodsByMoodId",
        tags = ["Mood"],
        path = "/api/moods/{mood-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("mood-id", Int::class, "The mood ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Mood::class)]), OpenApiResponse("404")]
    )
    fun getMoodsByMoodId(ctx: Context) {
        val mood = moodDAO.findByMoodId((ctx.pathParam("mood-id").toInt()))
        if (mood != null){
            ctx.json(mood)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete mood by mood ID",
        operationId = "deleteMoodByMoodId",
        tags = ["Mood"],
        path = "/api/moods/{mood-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("moods-id", Int::class, "The moods ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("404")]
    )
    fun deleteMoodByMoodId(ctx: Context){
        if (moodDAO.deleteByMoodId(ctx.pathParam("mood-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Delete mood by user ID",
        operationId = "deleteMoodByUserId",
        tags = ["Mood"],
        path = "/api/users/{user-id}/moods",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("404")]
    )
    fun deleteMoodByUserId(ctx: Context){
        if (moodDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update mood by mood ID",
        operationId = "updateMood",
        tags = ["Mood"],
        path = "/api/moods/{moods-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("moods-id", Int::class, "The mood ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("400"), OpenApiResponse("404")]
    )
    fun updateMood(ctx: Context){
        val mood : Mood = jsonToObject(ctx.body())
        if (!moodInputValidation(mood)){
            ctx.status(400)
        }
        else {
            if (moodDAO.updateByMoodId(
                    moodId = ctx.pathParam("mood-id").toInt(),
                    moodToUpdate = mood
                ) != 0
            )
                ctx.status(204)
            else
                ctx.status(404)
        }
    }

}