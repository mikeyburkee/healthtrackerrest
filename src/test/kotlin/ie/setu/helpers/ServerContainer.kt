package ie.setu.helpers

/**
 * Server container object used in test classes
 *
 * @author Michael Burke
 */

import ie.setu.config.JavalinConfig

object ServerContainer {

    val instance by lazy {
        startServerContainer()
    }

    private fun startServerContainer() = JavalinConfig().startJavalinService()

}