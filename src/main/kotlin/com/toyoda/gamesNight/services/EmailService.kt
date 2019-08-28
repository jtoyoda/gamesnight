package com.toyoda.gamesNight.services

import com.sendgrid.*
import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.Gamer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
class EmailService(val sendGrid: SendGrid) {
    @Value("\${url}")
    lateinit var url: String
    @Value("\${sendEmail}")
    lateinit var sendEmail: String

    fun inviteUser(event: GameEvent, gamer: Gamer) {
        gamer.email?.let {
            var contentString = "<div>Please respond to ${getDateFormat(event.date)}: ${event.name} <a href='$url' target='_'>here</a>.</div>"
            if (event.picker == gamer) {
                contentString += "<div>You are the Sommerlier this week!</div>"
            }
            sendEmail("${getDateFormat(event.date)}: ${event.name} - You are invited! ", "<html><body>$contentString</body></html>", it)
        }
    }

    fun uninviteUser(email: String, event: GameEvent) {
        val contentString = "Gaming Event ${getDateFormat(event.date)}: ${event.name} is no longer occurring. See other events <a href='$url' target='_'>here</a> "
        sendEmail("Gaming Event ${getDateFormat(event.date)}: ${event.name} has been cancelled",
                "<html><body>$contentString</body></html>",
                email
        )
    }

    fun sendSignupEmail(name: String?, email: String?) {
        if (name != null && email != null) {
            val contentString = "Please signup <a href='$url/signup?email=$email' target='_'>here</a>."
            sendEmail("Your invited for games night", "<html><body>$contentString</body></html>", email)
        }
    }

    fun notifyGameUpdate(email: String, event: GameEvent) {
        val contentString = "${event.picker?.name ?: "The Sommerlier"} has chosen ${event.game} for ${getDateFormat(event.date)}: ${event.name}"
        sendEmail("${getDateFormat(event.date)}: ${event.name} presents ${event.game}", "<html><body>$contentString</body></html>", email)

    }

    private fun sendEmail(subject: String, htmlContent: String, email: String) {
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

    private fun getDateFormat(timestamp: Timestamp?): String {
        return timestamp?.let {
            val instant = Instant.ofEpochMilli(it.time).atOffset(ZoneOffset.UTC)
            instant.format(DateTimeFormatter.ofPattern("MMM Do h:mma"))
        } ?: "Date TBD"
    }
}
