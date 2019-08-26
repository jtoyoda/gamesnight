package com.toyoda.gamesNight.services

import com.sendgrid.*
import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.Gamer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class EmailService(val sendGrid: SendGrid) {
    @Value("\${url}")
    lateinit var url: String
    @Value("\${sendEmail}")
    lateinit var sendEmail: String

    fun inviteUser(event: GameEvent, gamer: Gamer) {
        gamer.email?.let {
            val dateString = event.date?.let { timestamp ->
                val instant = Instant.ofEpochMilli(timestamp.time).atZone(ZoneId.systemDefault())
                instant.format(DateTimeFormatter.ofPattern("MMM dd"))
            } ?: "soon"
            var contentString = "Please respond <a href='$url' target='_'>here</a>."
            if (event.picker == gamer) {
                contentString += "\nYou are picking the game this week!"
            }
            sendEmail("New Gaming Event ${event.name} on $dateString", "<html><body>$contentString</body></html>", it)
        }
    }

    fun uninviteUser(event: GameEvent, gamer: Gamer) {
        gamer.email?.let {
            val dateString = event.date?.let {
                val instant = Instant.ofEpochMilli(it.time).atZone(ZoneId.systemDefault())
                instant.format(DateTimeFormatter.ofPattern("MMM dd"))
            } ?: ""
            val contentString = "This event is no longer occurring. See other events <a href='$url' target='_'>here</a> "
            sendEmail("Gaming Event ${event.name} $dateString Cancelled", "<html><body>$contentString</body></html>", it)
        }
    }

    fun sendEmail(subject: String, htmlContent: String, email: String) {
        if (sendEmail.toBoolean()) {
            val from = Email("boardGames@donotrespond.com")

            val to = Email(email)

            val content = Content("text/html", htmlContent)
            val mail = Mail(from, subject, to, content)

            val request = Request()
            try {
                request.method = Method.POST
                request.endpoint = "mail/send"
                request.body = mail.build()
                val response = sendGrid.api(request)
            } catch (ex: IOException) {
            }
        }
    }

    fun sendSignupEmail(name: String?, email: String?) {
        if (name != null && email != null) {
            val contentString = "Please signup <a href='$url/signup?email=$email' target='_'>here</a>."
            sendEmail("Your invited for games night", "<html><body>$contentString</body></html>", email)
        }
    }
}
