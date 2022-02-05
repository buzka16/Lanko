package android.example.tinkoff.network

import com.squareup.moshi.Json

data class Item(
    @Json(name = "description") var text: String = "",
    @Json(name = "gifURL") var img_src: String = ""
)