package com.kanta.gameinformationandroid.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Maker(
    val code: String, // APIの "code" に対応
    val name: String  // APIの "name" に対応
)