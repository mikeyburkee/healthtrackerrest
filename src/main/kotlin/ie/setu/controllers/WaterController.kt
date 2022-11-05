package ie.setu.controllers

import ie.setu.domain.Water
import ie.setu.domain.User
import ie.setu.domain.repository.WaterDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.*
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object WaterController{

    private val userDao = UserDAO()
    private val waterDAO = WaterDAO()

    @OpenApi(
        summary = "Get all waters",
        operationId = "getAllWaters",
        tags = ["Water"],
        path = "/api/waters",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Water>::class)])]
    )
    fun getAllWaters(ctx: Context) {
        val waters = waterDAO.getAll()
        if (waters.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(waters)
    }

    @OpenApi(
        summary = "Get waters by user ID",
        operationId = "getWatersByUserId",
        tags = ["Water"],
        path = "/api/users/{user-id}/waters",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Water::class)])]
    )
    fun getWatersByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val waters = waterDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (waters.isNotEmpty()) {
                ctx.json(waters)
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
        summary = "Add Water",
        operationId = "addWater",
        tags = ["Water"],
        path = "/api/waters",
        method = HttpMethod.POST,
        //pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200")]
    )
    fun addWater(ctx: Context) {
        val water : Water = jsonToObject(ctx.body())
        val userId = userDao.findById(water.userId)
        if (!waterInputValidation(water)){
            ctx.status(400)
        }
        else if (userId != null) {
            val waterId = waterDAO.save(water)
            water.id = waterId
            ctx.json(water)
            ctx.status(201)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Get water by water ID",
        operationId = "getWatersByWaterId",
        tags = ["Water"],
        path = "/api/waters/{water-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("water-id", Int::class, "The water ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getWatersByWaterId(ctx: Context) {
        val water = waterDAO.findByWaterId((ctx.pathParam("water-id").toInt()))
        if (water != null){
            ctx.json(water)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete water by water ID",
        operationId = "deleteWaterByWaterId",
        tags = ["Water"],
        path = "/api/waters/{water-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("water-id", Int::class, "The water ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteWaterByWaterId(ctx: Context){
        if (waterDAO.deleteByWaterId(ctx.pathParam("water-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Delete water by user ID",
        operationId = "deleteWaterByUserId",
        tags = ["Water"],
        path = "/api/users/{user-id}/waters",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteWaterByUserId(ctx: Context){
        if (waterDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update water by water ID",
        operationId = "updateWater",
        tags = ["Water"],
        path = "/api/waters/{water-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("water-id", Int::class, "The water ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateWater(ctx: Context){
        val water : Water = jsonToObject(ctx.body())
        if (!waterInputValidation(water)){
            ctx.status(400)
        }
        else if (waterDAO.updateByWaterId(
                waterId = ctx.pathParam("water-id").toInt(),
                waterToUpdate =water) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }
    
}