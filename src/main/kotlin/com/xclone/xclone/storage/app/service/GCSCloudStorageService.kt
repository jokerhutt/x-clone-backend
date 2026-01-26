package com.xclone.xclone.storage.app.service

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.xclone.xclone.storage.app.port.`in`.MediaStoragePort
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.io.InputStream
import java.time.Duration

class GCSCloudStorageService(
    private val gcs: Storage,
    private val bucketName: String
) : MediaStoragePort {

    override fun upload(
        key: String,
        inputStream: InputStream,
        contentType: String?,
        contentLength: Long
    ) {
        val blobId = BlobId.of(bucketName, key)

        val blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType)
            .build()

        gcs.create(blobInfo, inputStream)
    }

    override fun presignedGetUrl(key: String, expiresIn: Duration): String {
        return "https://storage.googleapis.com/$bucketName/$key"
    }

    override fun delete(key: String) {
        gcs.delete(BlobId.of(bucketName, key))
    }
}