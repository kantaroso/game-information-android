package com.kanta.gameinformationandroid.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MakerVideo(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String
)
