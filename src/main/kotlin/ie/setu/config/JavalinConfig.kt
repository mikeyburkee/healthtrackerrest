package ie.setu.config

/**
 * Javalin configuration class for health tracker app
 *
 * @author Michael Burke
 */

import ie.setu.controllers.*
import ie.setu.utils.jsonObjectMapper
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.plugin.json.JavalinJackson
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.javalin.plugin.rendering.vue.VueComponent
import io.swagger.v3.oas.models.info.Info

class JavalinConfig {

    fun startJavalinService(): Javalin {

        val app = Javalin.create {
            it.registerPlugin(getConfiguredOpenApiPlugin())
            it.defaultContentType = "application/json"
            //added this jsonMapper for our integration tests - serialise objects to json
            it.jsonMapper(JavalinJackson(jsonObjectMapper()))
            it.enableWebjars()
        }.apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(getRemoteAssignedPort())

        registerRoutes(app)
        return app

    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")

        // remote host
/*
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 7000

*/
        // local host
        return 7000
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {

            // ******************* VUE Endpoints ********************
            // the layout and display our <home-page> component.
            get("/", VueComponent("<home-page></home-page>"))

            //get("/users/{user-id}", VueComponent("<user-profile></user-profile>"))

            path ("/users"){
                get(VueComponent("<user-overview></user-overview>"))
                path("{user-id}"){
                    get(VueComponent("<user-profile></user-profile>"))
                    path("activities"){
                        get(VueComponent("<user-activity-overview></user-activity-overview>"))
                    }
                }
            }
            path("/activities"){
                get(VueComponent("<activities-overview></activities-overview>"))
                path("{activity-id}"){
                    get(VueComponent("<activity-profile></activity-profile>"))
                }
            }

            // ******************* API Endpoints ********************
            path("/api/users") {

                get(UserController::getAllUsers) // api tester complete
                post(UserController::addUser) // api tester complete
                path("{user-id}"){
                    get(UserController::getUserByUserId) // api tester complete
                    delete(UserController::deleteUser) // api tester complete
                    patch(UserController::updateUser) // api tester complete
                    path("activities"){
                        get(ActivityController::getActivitiesByUserId) // api tester complete
                        delete(ActivityController::deleteActivityByUserId) // api tester complete
//                        path("/duration/{sort}"){
//                            get(ActivityController::getActivitiesByUserIdWithDurationSorted)
//                        }
                    }
                    path("moods"){
                        get(MoodController::getMoodsByUserId) // api tester complete
                        delete(MoodController::deleteMoodByUserId) // api tester complete
                    }
                    path("sleeps"){
                        get(SleepController::getSleepsByUserId) // api tester complete
                        delete(SleepController::deleteSleepByUserId) // api tester complete
                    }
                    path("waters"){
                        get(WaterController::getWatersByUserId) //TODO api tester
                        delete(WaterController::deleteWaterByUserId) //TODO api tester
                    }
                }
                path("/email/{email}"){
                    get(UserController::getUserByEmail) // api tester complete
                }
            }
            path("/api/activities") {
                get(ActivityController::getAllActivities) // api tester complete
                post(ActivityController::addActivity) // api tester complete
                path("{activity-id}") {
                    get(ActivityController::getActivitiesByActivityId) // api tester complete
                    delete(ActivityController::deleteActivityByActivityId) // api tester complete
                    patch(ActivityController::updateActivity) // api tester complete
                }
                path ("{sort-param}"){
                    get(ActivityController::getAllActivitiesSortedAscending) // TODO get working with key
                }
                path("ascending"){
                    path("{sort-param}"){
                        get(ActivityController::getAllActivitiesSortedAscending) // TODO get key working
                    }
                    path("duration"){
                        get(ActivityController::getAllActivitiesSortedAscendingByDuration) // TODO api tester
                    }
                    path ("calories"){
                        get(ActivityController::getAllActivitiesSortedAscendingByCalories) // TODO api tester
                    }
                }
                path("descending"){
                    path("{sort-param}"){
                        get(ActivityController::getAllActivitiesSortedDescending) // TODO get key working
                    }
                    path("duration"){
                        get(ActivityController::getAllActivitiesSortedDescendingByDuration) // TODO api tester
                    }
                    path ("calories"){
                        get(ActivityController::getAllActivitiesSortedDescendingByCalories) // TODO api tester
                    }
                }
            }
            path("/api/moods"){
                get(MoodController::getAllMoods) // api tester complete
                post(MoodController::addMood) // api tester complete
                path("{mood-id}") {
                    get(MoodController::getMoodsByMoodId) // api tester complete
                    delete(MoodController::deleteMoodByMoodId) // api tester complete
                    patch(MoodController::updateMood) // api tester complete
                }
            }
            path("/api/sleeps"){
                get(SleepController::getAllSleeps) // api tester complete
                post(SleepController::addSleep) // api tester complete
                path("{sleep-id}") {
                    get(SleepController::getSleepsBySleepId) // api tester complete
                    delete(SleepController::deleteSleepBySleepId) // api tester complete
                    patch(SleepController::updateSleep) // api tester complete
                }
            }
            path("/api/waters"){
                get(WaterController::getAllWaters) // api tester complete
                post(WaterController::addWater) // api tester complete
                path("{water-id}") {
                    get(WaterController::getWatersByWaterId) // api tester complete
                    delete(WaterController::deleteWaterByWaterId) // api tester complete
                    patch(WaterController::updateWater) // api tester complete
                }
            }
        }
    }

    fun getConfiguredOpenApiPlugin() = OpenApiPlugin(
        OpenApiOptions(
            Info().apply {
                title("Health Tracker App")
                version("2.0")
                description("Health Tracker API")
            }
        ).apply {
            path("/swagger-docs") // endpoint for OpenAPI json
            swagger(SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
            reDoc(ReDocOptions("/redoc")) // endpoint for redoc
        }
    )
}