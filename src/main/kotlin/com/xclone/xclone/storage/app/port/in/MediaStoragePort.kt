package com.xclone.xclone.storage.app.port.`in`

import java.io.InputStream
import java.time.Duration

interface MediaStoragePort {
    fun upload(
        key: String,
        inputStream: InputStream,
        contentType: String?,
        contentLength: Long
    )

    fun presignedGetUrl(
        key: String,
        expiresIn: Duration = Duration.ofHours(6)
    ): String

    fun delete(key: String)
}