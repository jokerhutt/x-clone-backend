package com.xclone.xclone.helpers
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.TestingAuthenticationToken

object MvcTestHelper {

    fun authenticationToken(authenticatedUserId: Int): TestingAuthenticationToken {
        return TestingAuthenticationToken(authenticatedUserId, null)
    }

    fun toJson(objectMapper: ObjectMapper, objectToSerialize: Any): String {
        return try {
            objectMapper.writeValueAsString(objectToSerialize)
        } catch (exception: Exception) {
            throw RuntimeException(exception)
        }
    }
}