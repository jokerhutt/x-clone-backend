package com.xclone.xclone.storage.configuration

import com.google.cloud.storage.Storage
import com.xclone.xclone.storage.configuration.StorageProperties
import com.xclone.xclone.storage.app.port.`in`.MediaStoragePort
import com.xclone.xclone.storage.app.service.GCSCloudStorageService
import com.xclone.xclone.storage.app.service.S3CloudStorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class StorageConfig {

    @Bean
    @ConditionalOnProperty(prefix = "storage", name = ["mode"], havingValue = "gcs")
    fun gcsStorageService(gcsClient: Storage, props: StorageProperties): MediaStoragePort =
        GCSCloudStorageService(gcsClient, props.gcs.bucket)

    @Bean
    @ConditionalOnProperty(prefix = "storage", name = ["mode"], havingValue = "s3")
    fun s3StorageService(
        s3Client: S3Client,
        presigner: S3Presigner,
        props: StorageProperties
    ): MediaStoragePort =
        S3CloudStorageService(s3Client, presigner, props.s3.bucket)
}