package com.xclone.xclone.storage
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.StorageOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class CloudStorageService(

    @Value("\${gcs.bucket}")
    private val bucketName: String

) {

    fun upload(fileName: String, inputStream: InputStream, contentType: String?): String {
        val storage = StorageOptions.getDefaultInstance().service

        val blobId = BlobId.of(bucketName, fileName)
        val blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(contentType)
            .build()

        storage.create(blobInfo, inputStream)

        return "https://storage.googleapis.com/$bucketName/$fileName"
    }
}