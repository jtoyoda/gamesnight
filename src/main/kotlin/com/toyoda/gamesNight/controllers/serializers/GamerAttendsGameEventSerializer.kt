package com.toyoda.gamesNight.controllers.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.toyoda.gamesNight.database.models.GamerAttendsGameEvent

class GamerAttendsGameEventSerializer : JsonSerializer<MutableList<GamerAttendsGameEvent>>() {
    override fun serialize(value: MutableList<GamerAttendsGameEvent>?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (value != null) {
            gen!!.writeObject(value.map { gamerAttendsGameEvent ->
                GamerPlusStatus(gamerAttendsGameEvent.gamer?.id, gamerAttendsGameEvent.gamer?.name,
                        gamerAttendsGameEvent.gamer?.email, gamerAttendsGameEvent.attending)
            })
        }
    }

}

data class GamerPlusStatus(val id: Int?, val name: String?, val email: String?, val attending: Boolean?)
