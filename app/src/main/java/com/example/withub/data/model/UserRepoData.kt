package com.example.withub.data.model

import com.google.gson.annotations.SerializedName

data class UserRepoData(
    @SerializedName("owner") var owner:String,
    @SerializedName("name") var name: String
)
