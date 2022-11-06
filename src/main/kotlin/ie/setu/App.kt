package ie.setu

/**
 * Application starter file
 *
 * @author Michael Burke
 */

import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig

fun main() {

    DbConfig().getDbConnection()
    JavalinConfig().startJavalinService()

}