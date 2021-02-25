package com.moonm

import kotlinx.serialization.Serializable

@Serializable
data class Action(
    val clientExtra: ClientExtra,
    val detailParams: DetailParams,
    val id: String,
    val name: String,
    val params: Params
)

@Serializable
data class Block(
    val id: String,
    val name: String
)

@Serializable
data class Bot(
    val id: String,
    val name: String
)

@Serializable
class ClientExtra(
)

@Serializable
data class DetailParams(
    val meal_menu: MealMenu,
    val sys_date: SysDate
)

@Serializable
data class Extra(
    val reason: Reason
)

@Serializable
data class Intent(
    val extra: Extra,
    val id: String,
    val name: String
)

@Serializable
data class MealMenu(
    val groupName: String,
    val origin: String,
    val value: String
)

@Serializable
data class Params(
    val meal_menu: String,
    val sys_date: String
)

@Serializable
data class ParamsX(
    val ignoreMe: String,
    val surface: String
)

@Serializable
data class Properties(
    val botUserKey: String,
    val bot_user_key: String
)

@Serializable
data class Reason(
    val code: Int,
    val message: String
)

@Serializable
data class ReceiveData(
    val action: Action,
    val bot: Bot,
    val contexts: List<String>,
    val intent: Intent,
    val userRequest: UserRequest
)

@Serializable
data class SysDate(
    val groupName: String,
    val origin: String,
    val value: String
)

@Serializable
data class User(
    val id: String,
    val properties: Properties,
    val type: String
)

@Serializable
data class UserRequest(
    val block: Block,
    val lang: String,
    val params: ParamsX,
    val timezone: String,
    val user: User,
    val utterance: String
)