package ie.setu.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun mapJSONDate() = (
    jacksonObjectMapper()
    .registerModule(JodaModule())
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
)

