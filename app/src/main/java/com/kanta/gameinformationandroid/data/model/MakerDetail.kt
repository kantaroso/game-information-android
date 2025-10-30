package com.kanta.gameinformationandroid.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MakerDetail(
    @SerialName("code")
    val code: String,
    @SerialName("name")
    val name: String,
    @SerialName("ohp")
    val ohp: String? = null,
    @SerialName("twitter_name")
    val twitterScreenName: String? = null
)
