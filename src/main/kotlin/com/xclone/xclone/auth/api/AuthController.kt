package com.xclone.xclone.auth.api

import com.xclone.xclone.auth.app.service.AuthService
import com.xclone.xclone.auth.app.service.JwtService
import com.xclone.xclone.commons.constants.ApiPaths
import com.xclone.xclone.user.api.dto.UserDTO
import com.xclone.xclone.user.app.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping(ApiPaths.AUTH.BASE)
class AuthController(
    private val authService: AuthService,
    private val userService: UserService,
    private val jwtService: JwtService
) {

    @PostMapping(ApiPaths.AUTH.GOOGLE_LOGIN)
    fun authenticateWithGoogle(@RequestBody body: Map<String, String>): ResponseEntity<Any> {
        val accessToken = body["token"] ?: throw IllegalArgumentException("Missing token")

        val authenticatedUser = authService.authenticateGoogleUser(accessToken)
        val dtoToReturn = userService.generateUserDTOByUserId(authenticatedUser.id!!)

        val token = jwtService.createToken(dtoToReturn.id!!)
        return ResponseEntity.ok(
            mapOf(
                "token" to token,
                "user" to dtoToReturn
            )
        )
    }

    @PostMapping(ApiPaths.AUTH.UPDATE_PROFILE)
    @Throws(IOException::class)
    fun updateProfile(
        @RequestParam(value = "profilePicture", required = false) profilePicture: MultipartFile?,
        @RequestParam(value = "bannerImage", required = false) bannerImage: MultipartFile?,
        @RequestParam("displayName") displayName: String,
        @RequestParam("username") username: String,
        @RequestParam("bio") bio: String,
        auth: Authentication
    ): ResponseEntity<UserDTO> {
        val authUserId = auth.principal as Int
        userService.updateUserProfile(authUserId, profilePicture, bannerImage, displayName, username, bio)
        return ResponseEntity.ok(userService.generateUserDTOByUserId(authUserId))
    }

    @GetMapping(ApiPaths.AUTH.AUTH_ME)
    fun getAuthenticatedUser(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<Any> {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header")
        }

        val token = authHeader.removePrefix("Bearer ").trim()
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token")
        }

        val userId = jwtService.extractUserId(token)
        val dto = userService.generateUserDTOByUserId(userId)

        return ResponseEntity.ok(dto)
    }

    @PostMapping(ApiPaths.AUTH.DEMO_SIGNUP)
    fun authenticateWithTempSignup(): ResponseEntity<Any> {
        val newTempUser = authService.registerTemporaryUser()
        val dtoToReturn = userService.generateUserDTOByUserId(newTempUser.id!!)
        val token = jwtService.createToken(newTempUser.id!!)

        return ResponseEntity.ok(
            mapOf(
                "token" to token,
                "user" to dtoToReturn
            )
        )
    }


}