package model

import kotlinx.serialization.*

@Serializable
data class DlanUrlDTO(
    var title: String?,
    var url: String?,
    var headers: Map<String, String>?,
    var jumpStartDuration: Int,
    var jumpEndDuration: Int
) {

    constructor() : this(null, null, null, 0, 0) {

    }

    constructor(jumpStartDuration: Int, jumpEndDuration: Int) : this(
        null,
        null,
        null,
        jumpStartDuration,
        jumpEndDuration
    ) {
        this.jumpStartDuration = jumpStartDuration
        this.jumpEndDuration = jumpEndDuration
    }

    constructor(url: String?, headers: Map<String, String>?, jumpStartDuration: Int, jumpEndDuration: Int)
            : this(null, url, headers, jumpStartDuration, jumpEndDuration) {
        this.url = url
        this.headers = headers
        this.jumpStartDuration = jumpStartDuration
        this.jumpEndDuration = jumpEndDuration
    }
}