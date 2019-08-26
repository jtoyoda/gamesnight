package com.toyoda.gamesNight.controllers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.toyoda.gamesNight.database.models.GamerInGameNight

class GamerInGameNightSerializer: JsonSerializer<MutableList<GamerInGameNight>>() {
    override fun serialize(value: MutableList<GamerInGameNight>?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (value != null) {
            gen!!.writeObject(value.map { it.gamer })
        }
    }

}
