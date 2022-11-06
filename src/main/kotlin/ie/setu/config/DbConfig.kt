package ie.setu.config

/**
 * Database configuration class for health tracker app
 *
 * @author Michael Burke
 */

import org.jetbrains.exposed.sql.Database

class DbConfig{

    //NOTE: you need the ?sslmode=require otherwise you get an error complaining about the ssl certificate
    fun getDbConnection() :Database{
        return Database.connect(
            "jdbc:postgresql://ec2-52-4-87-74.compute-1.amazonaws.com:5432/debnjdk13qdkvl?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "ctenpkjcocylba",
            password = "e88fc0639cb4cdb5d425b7fcfc5d63ebc6524a068dce2756e04a55896a12ad9f")
    }

}