package com.xclone.xclone.storage.configuration

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.NoCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@ConditionalOnProperty(prefix = "storage", name = ["mode"], havingValue = "gcs")
@Profile("!test")
class GcpClientConfig(
    private val props: StorageProperties
) {

    @Bean
    fun gcsClient(): Storage {
        val gcs = props.gcs

        val builder = StorageOptions.newBuilder()
            .setProjectId(gcs.projectId)

        if (gcs.host.isNotBlank()) {
            builder.setHost(gcs.host)
            builder.setCredentials(NoCredentials.getInstance())
        } else {
            builder.setCredentials(GoogleCredentials.getApplicationDefault())
        }

        return builder.build().service
    }
}