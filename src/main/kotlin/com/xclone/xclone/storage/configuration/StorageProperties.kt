package com.xclone.xclone.storage.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "storage")
class StorageProperties (
    var mode: Mode = Mode.S3,
    var gcs: Gcs = Gcs(),
    var s3: S3 = S3()
) {
    enum class Mode {GCS, S3}

    data class Gcs(
        var projectId: String = "",
        var bucket: String = "",
        var host: String = "",
        var credentialsJson: String = ""
    )

    data class S3(
        var region: String = "auto",
        var bucket: String = "",
        var endpoint: String = ""
    )



}