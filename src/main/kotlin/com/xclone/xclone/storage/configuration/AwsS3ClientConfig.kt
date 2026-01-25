package com.xclone.xclone.storage.configuration

import com.xclone.xclone.storage.configuration.StorageProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
@ConditionalOnProperty(prefix = "storage", name = ["mode"], havingValue = "s3")
@Profile("!test")
class AwsS3ClientConfig(
    private val props: StorageProperties
) {

    @Bean
    fun s3Client(): S3Client {
        val s3 = props.s3
        val endpoint = s3.endpoint?.takeIf { it.isNotBlank() }

        val builder = S3Client.builder()
            .region(Region.of(s3.region))

        if (endpoint != null) {
            builder
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(
                    S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
        }

        return builder.build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val s3 = props.s3
        val endpoint = s3.endpoint.takeIf { it.isNotBlank() }

        val builder = S3Presigner.builder()
            .region(Region.of(s3.region))

        if (endpoint != null) {
            builder.endpointOverride(URI.create(endpoint))
        }

        return builder.build()
    }


}