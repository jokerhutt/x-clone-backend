package com.xclone.xclone.storage.app.service

import com.xclone.xclone.storage.app.port.`in`.MediaStoragePort
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.io.InputStream
import java.time.Duration


class S3CloudStorageService(
    private val s3Client: S3Client,
    private val presigner: S3Presigner,
    private val bucketName: String
) : MediaStoragePort {

    override fun upload(
        key: String,
        inputStream: InputStream,
        contentType: String?,
        contentLength: Long
    ) {
        val putReq = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build()

        s3Client.putObject(
            putReq,
            RequestBody.fromInputStream(inputStream, contentLength)
        )
    }

    override fun presignedGetUrl(key: String, expiresIn: Duration): String {
        val getReq = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        val presignReq = GetObjectPresignRequest.builder()
            .signatureDuration(expiresIn)
            .getObjectRequest(getReq)
            .build()

        return presigner.presignGetObject(presignReq).url().toString()
    }

    override fun delete(key: String) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
        )
    }
}