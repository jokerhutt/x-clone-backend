package com.xclone.xclone.domain.auth
import com.xclone.xclone.domain.user.UserDTO
import com.xclone.xclone.domain.user.UserService
import com.xclone.xclone.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val userService: UserService,
    private val jwtService: JwtService
) {

    @PostMapping("/google-login")
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

    @PostMapping("/update-profile")
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

    @GetMapping("/me")
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

    @PostMapping("/demo-signup")
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