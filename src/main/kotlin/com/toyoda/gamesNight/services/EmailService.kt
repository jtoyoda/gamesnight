package com.toyoda.gamesNight.services

import com.sendgrid.*
import com.toyoda.gamesNight.database.models.GameEvent
import com.toyoda.gamesNight.database.models.Gamer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.sql.Timestamp
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

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
                contentString += "<div>You are the Sommelier this week!</div>"
            } else if (event.game != null) {
                contentString += "<div>${event.picker?.name
                        ?: "The Sommelier"} has chosen <strong>${event.game}</strong></div>"
            }
            sendEmail("${getDateFormat(event.date)}: ${event.name} - You are invited! ", "<html><body>$contentString</body></html>", it)
        }
    }

    fun uninviteUser(email: String, event: GameEvent) {
        val contentString = "${getDateFormat(event.date)}: ${event.name} is no longer occurring. See other events <a href='$url' target='_'>here</a> "
        sendEmail("${getDateFormat(event.date)}: ${event.name} has been cancelled",
                "<html><body>$contentString</body></html>",
                email
        )
    }

    fun sendSignupEmail(name: String?, email: String?) {
        if (name != null && email != null) {
            val contentString = "Please complete your registration <a href='$url/signup?email=$email' target='_'>here</a>."
            sendEmail("Your invited to a games night scheduler", "<html><body>$contentString</body></html>", email)
        }
    }

    fun notifyEventUpdate(email: String, event: GameEvent, gameChanged: Boolean, timeChanged: Boolean, pickerChanged: Boolean) {
        val newGameStringContent = "${event.picker?.name
                ?: "The Sommelier"} has chosen <strong>${event.game}</strong>. "
        val newTimeContent = "The time has been updated to ${getDateFormat(event.date)}. "
        val pickerContent = if (email == event.picker?.email)
            "You are now the Sommelier. "
        else
            "${event.picker?.name ?: "Nobody"} is now the Sommelier. "

        var contentString = if (gameChanged) newGameStringContent else ""
        if (timeChanged) {
            contentString = "$contentString$newTimeContent"
        }
        if (pickerChanged) {
            contentString = "$contentString$pickerContent"
        }
        contentString = "$contentString See the event <a href='$url' target='_'>here</a>"
        sendEmail("Update! ${if (timeChanged) "New Time " else ""}${getDateFormat(event.date)}: ${event.name} presents ${event.game}", "<html><body>$contentString</body></html>", email)
    }

    private fun sendEmail(subject: String, htmlContent: String, email: String) {
        if (sendEmail.toBoolean()) {
            val from = Email("boardGames@donotrespond.com", "Board Game Events")

            val to = Email(email)

            val content = Content("text/html", htmlContent)
            val mail = Mail(from, subject, to, content)

            val request = Request()
            try {
                request.method = Method.POST
                request.endpoint = "mail/send"
                request.body = mail.build()
                sendGrid.api(request)
            } catch (ex: IOException) {
            }
        }
    }

    private fun getDateFormat(timestamp: Timestamp?): String {
        return timestamp?.let {
            val instant = Instant.ofEpochMilli(it.time).atZone(TimeZone.getTimeZone("America/Denver").toZoneId())
            instant.format(DateTimeFormatter.ofPattern("MMM d h:mma"))
        } ?: "Date TBD"
    }
}
