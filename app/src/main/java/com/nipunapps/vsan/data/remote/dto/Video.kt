package com.nipunapps.vsan.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class Video (
    val title : String,
    val id : Int,
    val videoLink : String,
    val imageLink : String
) : java.io.Serializable
