package com.moonm

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.go.neis.api.Menu
import kr.go.neis.api.School
import org.slf4j.event.*
import java.text.SimpleDateFormat

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
val school = School.find(School.Region.GYEONGGI, "상원고등학교")
val cachedData:HashMap<String,Menu> = hashMapOf()

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    routing {
        post("/lunch") {
            val params = call.receiveText()
            println(params)

            var received:ReceiveData? = null
            try {
                received = Json.decodeFromString<ReceiveData>(params)
            } catch (e:Exception) {
                e.printStackTrace()
            }

            val date = if (received==null) Date.today() else Date.parse(Json.decodeFromString<Map<String,String?>>(received.action.detailParams.sys_date.value)["date"]!!)

            val lunch = if (cachedData.containsKey(date.serialize())) {
                cachedData[date.serialize()]!!.lunch
            } else {
                val monthly = school.getMonthlyMenu(date.year,date.month)
                val menu = monthly[date.date-1]
                cachedData[date.serialize()] = menu
                menu.lunch
            }
            println("cached= $cachedData")

            val data = Response(
                version = "2.0",
                template = Outputs(
                    outputs = listOf(
                        Output(
                            simpleText = SimpleText(
                                text = lunch
                            )
                        )
                    )
                )
            )
            val respond = Json.encodeToString(data)
            println(respond)
            call.respondText(respond)
        }
    }
}

@Serializable
data class Response(
    val version:String,
    val template:Outputs
)

@Serializable
data class Output(
    val simpleText: SimpleText
)

@Serializable
data class Outputs(
    val outputs: List<Output>
)

@Serializable
data class SimpleText(
    val text: String
)

data class Date(
    val year:Int,
    val month:Int,
    val date:Int
) {
    companion object {
        fun parse(raw:String):Date {
            val spl = raw.split("-")
            return Date(
                year = spl[0].toInt(),
                month = spl[1].toInt(),
                date = spl[2].toInt()
            )
        }

        fun today():Date {
            val format = SimpleDateFormat("yyyy-MM-dd")
            return parse(format.format(java.util.Date()))
        }
    }

    fun serialize():String {
        return "$year-$month-$date"
    }
}